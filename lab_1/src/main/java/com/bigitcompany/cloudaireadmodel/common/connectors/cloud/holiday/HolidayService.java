package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.holiday;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Holiday;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineStatsCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class HolidayService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String HOLIDAYS_CACHE_METRICS_NAME = "holidays_cache_counter";

    private final HolidayConnector holidayConnector;

    private final Cache<String, List<Holiday>> cache;

    public HolidayService(HolidayConnector holidayConnector,
                          @Qualifier("prometheusMeterRegistry") MeterRegistry meterRegistry,
                          @Value("${service.holiday.cache.metrics.enabled}") boolean cacheMetricsEnabled,
                          @Value("${service.holiday.cache.initial:1000}") int initialCapacity,
                          @Value("${service.holiday.cache.max:50000}") int maxCapacity) {

        this.holidayConnector = holidayConnector;

        var cacheStatsCounter = cacheMetricsEnabled ?
            new CaffeineStatsCounter(meterRegistry, HOLIDAYS_CACHE_METRICS_NAME) :
            StatsCounter.disabledStatsCounter();

        cache = Caffeine.newBuilder()
            .initialCapacity(initialCapacity)
            .maximumSize(maxCapacity)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .recordStats(() -> cacheStatsCounter)
            .build();

        if (cacheStatsCounter instanceof CaffeineStatsCounter caffeineStatsCounter) {
            caffeineStatsCounter.registerSizeMetric(cache);
        }
    }

    public CompletableFuture<Map<UUID, List<Holiday>>> fetchHolidays(String tenant,
                                                                     List<UUID> technicianIds,
                                                                     Instant earliestBooking,
                                                                     Instant latestBooking,
                                                                     boolean useCache) {
        if (technicianIds.isEmpty()) {
            LOG.warn("Empty technicianIds list");
            return CompletableFuture.completedFuture(Collections.emptyMap());
        }

        var periodStart = startOfTheYear(earliestBooking);
        var periodEnd = latestBooking == null ? endOfTheYear(Instant.now()) : endOfTheYear(latestBooking);
        if (periodStart.isAfter(periodEnd)) {
            LOG.warn("earliestBooking is after the latestBooking, skip fetching holidays");
            return CompletableFuture.completedFuture(Collections.emptyMap());
        }

        tenant = tenant.trim();
        return fetchCacheableHolidays(tenant, technicianIds, periodStart, periodEnd, useCache);
    }

    private CompletableFuture<Map<UUID, List<Holiday>>> fetchCacheableHolidays(String tenant,
                                                                               List<UUID> technicianIds,
                                                                               Instant earliestBooking,
                                                                               Instant latestBooking,
                                                                               boolean useCache) {
        if (!useCache) {
            invalidateCache(tenant, technicianIds, earliestBooking, latestBooking);
            return holidayConnector.fetchHolidays(technicianIds, earliestBooking, latestBooking);
        }

        Map<UUID, List<Holiday>> response = new HashMap<>(technicianIds.size());
        List<UUID> technicianIdsForFetching = new ArrayList<>();
        int[] sequenceOfYearsBetweenInstants = sequenceOfYearsBetweenInstants(earliestBooking, latestBooking);

        // for each technician aggregate holidays for given sequence of years between earliest and latest bookings
        for (UUID techId : technicianIds) {
            for (int year : sequenceOfYearsBetweenInstants) {
                List<Holiday> technicianHolidaysForParticularYear = getFromCache(tenant, techId, year);

                // if holidays for this particular year and technician was cached, then we don't need
                // to make a request for this technician to the CloudHolidayService
                if (technicianHolidaysForParticularYear != null) {

                    if (response.containsKey(techId)) {
                        response.get(techId).addAll(technicianHolidaysForParticularYear);
                    } else {
                        response.put(techId, new ArrayList<>(technicianHolidaysForParticularYear));
                    }

                    // if holidays for this particular year and technician was not cached
                    // , then we add technicianId for fetching
                    // actually it means, we invalidate every holiday for him
                } else {
                    technicianIdsForFetching.add(techId);
                    break;
                }
            }
        }

        if (technicianIdsForFetching.isEmpty()) {
            // we ' ve aggregated all the holidays for each technician from the cache
            return CompletableFuture.completedFuture(response);
        }

        return holidayConnector.fetchHolidays(technicianIdsForFetching, earliestBooking, latestBooking)
            .thenApply(holidaysMap -> {
                response.putAll(holidaysMap);
                cacheTheResult(holidaysMap, tenant);
                return response;
            });
    }

    private void invalidateCache(String tenant, List<UUID> technicianIds, Instant earliestBooking, Instant latestBooking) {
        int[] sequenceOfYearsBetweenInstants = sequenceOfYearsBetweenInstants(earliestBooking, latestBooking);
        for (UUID technician : technicianIds) {
            for (int year : sequenceOfYearsBetweenInstants) {
                String key = cacheKey(tenant, technician, year);
                cache.invalidate(key);
            }
        }
    }

    @Nullable
    private List<Holiday> getFromCache(String tenant, UUID techId, int year) {
        String key = cacheKey(tenant, techId, year);
        return cache.getIfPresent(key);
    }

    private void cacheTheResult(Map<UUID, List<Holiday>> holidaysMap, String tenant) {
        for (Map.Entry<UUID, List<Holiday>> uuidHolidaysEntry : holidaysMap.entrySet()) {
            var holidays = uuidHolidaysEntry.getValue();
            var technicianId = uuidHolidaysEntry.getKey();
            if (holidays != null && !holidays.isEmpty()) {
                Map<Integer, List<Holiday>> yearToHolidaysMap = partitioningHolidaysByYear(holidays);
                for (Map.Entry<Integer, List<Holiday>> holidaysEntry : yearToHolidaysMap.entrySet()) {
                    String key = cacheKey(tenant, technicianId, holidaysEntry.getKey());
                    cache.put(key, holidaysEntry.getValue());
                }
            }
        }
    }

    private Map<Integer, List<Holiday>> partitioningHolidaysByYear(List<Holiday> holidays) {
        Map<Integer, List<Holiday>> yearToHolidaysMap = new HashMap<>();
        for (Holiday holiday : holidays) {
            Integer year = holiday.getDay().getYear();
            if (yearToHolidaysMap.containsKey(year)) {
                yearToHolidaysMap.get(year).add(holiday);
            } else {
                List<Holiday> holidaysArrayList = new ArrayList<>();
                holidaysArrayList.add(holiday);
                yearToHolidaysMap.put(year, holidaysArrayList);
            }
        }
        return yearToHolidaysMap;
    }

    private int[] sequenceOfYearsBetweenInstants(Instant earliestBooking, Instant latestBooking) {
        var calendar = Calendar.getInstance();

        calendar.setTimeInMillis(earliestBooking.toEpochMilli());
        int earliestBookingYear = calendar.get(Calendar.YEAR);

        calendar.setTimeInMillis(latestBooking.toEpochMilli());
        int latestBookingYear = calendar.get(Calendar.YEAR);

        final var bookingYearsDifference = latestBookingYear - earliestBookingYear + 1;
        if (bookingYearsDifference > 0) {
            var sequenceOfYears = new int[bookingYearsDifference];
            for (var i = 0; i < bookingYearsDifference; i++) {
                sequenceOfYears[i] = earliestBookingYear + i;
            }
            return sequenceOfYears;
        }
        return new int[]{};
    }


    private Instant startOfTheYear(Instant instant) {
        instant = instant.truncatedTo(ChronoUnit.DAYS);
        var startOfTheYear = Calendar.getInstance();
        startOfTheYear.setTimeInMillis(instant.toEpochMilli());
        startOfTheYear.set(Calendar.DAY_OF_YEAR, 1);
        return startOfTheYear.toInstant();
    }


    private Instant endOfTheYear(Instant instant) {
        instant = instant.truncatedTo(ChronoUnit.DAYS);
        var endOfTheYear = Calendar.getInstance();
        endOfTheYear.setTimeInMillis(instant.toEpochMilli());
        endOfTheYear.add(Calendar.YEAR, 1);
        endOfTheYear.set(Calendar.DAY_OF_YEAR, -1);
        return endOfTheYear.toInstant();
    }

    @NotNull
    private String cacheKey(@NotNull String tenant, @NotNull UUID techId, int year) {
        return tenant + '/' + techId + '/' + year;
    }
}