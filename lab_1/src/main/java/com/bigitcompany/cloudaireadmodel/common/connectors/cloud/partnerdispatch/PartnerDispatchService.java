package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.partnerdispatch;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.StatsCounter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineStatsCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class PartnerDispatchService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String PARTNER_DISPATCH_CACHE_METRICS_NAME = "resource_exclude_list_cache_counter";

    private final PartnerDispatchConnector partnerDispatchConnector;

    private final Cache<String, List<UUID>> cache;

    private final int excludeListsFutureTimeoutInMs;

    public PartnerDispatchService(PartnerDispatchConnector partnerDispatchConnector,
                                  @Qualifier("prometheusMeterRegistry") MeterRegistry meterRegistry,
                                  @Value("${service.partner-dispatch.cache.metrics.enabled}") boolean cacheMetricsEnabled,
                                  @Value("${service.partner-dispatch.cache.initial:1000}") int initialCapacity,
                                  @Value("${service.partner-dispatch.cache.max:50000}") int maxCapacity,
                                  @Value("${service.partner-dispatch.futureTimeoutMs:20000}") int excludeListsFutureTimeoutInMs) {

        this.partnerDispatchConnector = partnerDispatchConnector;
        this.excludeListsFutureTimeoutInMs = excludeListsFutureTimeoutInMs;

        var cacheStatsCounter = cacheMetricsEnabled ?
            new CaffeineStatsCounter(meterRegistry, PARTNER_DISPATCH_CACHE_METRICS_NAME) :
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

    public CompletableFuture<Map<UUID, List<UUID>>> fetchResourceExcludeList(List<UUID> jobIds, String tenant, boolean useExcludeList) {
        if (jobIds.isEmpty() || !useExcludeList) {
            return CompletableFuture.completedFuture(Collections.emptyMap());
        }

        return fetchCacheableResourceExcludeList(jobIds, tenant);
    }

    public Map<UUID, List<UUID>> getExcludeMapFromFutureWithFallbackToEmptyMap(CompletableFuture<Map<UUID, List<UUID>>> resourceExcludeListFuture) {
        try {
            return resourceExcludeListFuture.get(excludeListsFutureTimeoutInMs, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | TimeoutException e) {
            LOG.error("Exception while fetching jobs excludeLists", e);
        } catch (InterruptedException e) {
            LOG.error("InterruptedException while jobs excludeLists", e);
            Thread.currentThread().interrupt();
        }
        return Collections.emptyMap();
    }

    private CompletableFuture<Map<UUID, List<UUID>>> fetchCacheableResourceExcludeList(List<UUID> jobIds, String tenant) {
        Map<UUID, List<UUID>> response = new HashMap<>(jobIds.size());
        List<UUID> resourceExcludeListToFetch = new ArrayList<>();

        for (UUID jobId : jobIds) {
            List<UUID> resourceExcludeListFromCache = cache.getIfPresent(cacheKey(tenant, jobId));
            if (resourceExcludeListFromCache != null) {
                if (response.containsKey(jobId)) {
                    response.get(jobId).addAll(resourceExcludeListFromCache);
                } else {
                    response.put(jobId, new ArrayList<>(resourceExcludeListFromCache));
                }
            } else {
                resourceExcludeListToFetch.add(jobId);
                break;
            }
        }

        if (resourceExcludeListToFetch.isEmpty()) {
            return CompletableFuture.completedFuture(response);
        }

        return partnerDispatchConnector.fetchResourceExcludeList(resourceExcludeListToFetch)
            .thenApply(resourceExcludeList -> {
                response.putAll(resourceExcludeList);
                cacheTheResult(resourceExcludeList, tenant);
                return response;
            });
    }

    private void cacheTheResult(Map<UUID, List<UUID>> resourceExcludeList, String tenant) {
        for (Map.Entry<UUID, List<UUID>> resourceExcludeListEntry : resourceExcludeList.entrySet()) {
            var excludeList = resourceExcludeListEntry.getValue();
            var jobId = resourceExcludeListEntry.getKey();
            if (excludeList != null) {
                String key = cacheKey(tenant, jobId);
                cache.put(key, excludeList);
            }
        }
    }

    @NotNull
    private String cacheKey(@NotNull String tenant, @NotNull UUID jobId) {
        return tenant + '/' + jobId;
    }
}
