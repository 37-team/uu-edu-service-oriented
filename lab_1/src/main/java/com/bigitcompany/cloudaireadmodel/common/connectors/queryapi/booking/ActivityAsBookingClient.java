package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingJob;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ExecutionStage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue.udfValuesToMap;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ACTIVITY;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ADDRESS;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.BUSINESSPARTNER;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.EQUIPMENT;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SERVICECALL;

@Component
public class ActivityAsBookingClient extends AbstractQueryApiClient {
    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(ACTIVITY, ADDRESS, SERVICECALL, EQUIPMENT, BUSINESSPARTNER, PERSON);

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    protected ActivityAsBookingClient(@Value("${service.query-api.host}") String queryApiHost,
                                      @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<Booking>>> queryActivityAsBookingByPersonIds(ReadModelRequestContext requestContext,
                                                                                         List<UUID> resourceIds,
                                                                                         BookingsFilter filter) {
        var body = createQueryBody(resourceIds, filter);
        List<ActivityAsBookingDto> pages = getAllPages(requestContext, REQUEST_DTOS, body, ActivityAsBookingDto.class);
        return CompletableFuture.completedFuture(unwrapPagesByPersonId(pages, filter));
    }

    private Map<String, String> createQueryBody(List<UUID> resourceIds, BookingsFilter filter) {
        var sqlSelect = """
                        SELECT DISTINCT\s
                            person.id,\s
                            activity.id,\s
                            activity.lastChanged,\s
                            activity.executionStage,\s
                            activity.startDateTime,\s
                            activity.endDateTime,\s
                            activity.udfValues,\s
                            serviceCall.id,\s
                            serviceCall.udfValues,\s
                            equipment.id,\s
                            equipment.udfValues,\s
                            businessPartner.id,\s
                            businessPartner.udfValues,\s
                            address.location\s
                        FROM Activity activity\s
                        INNER JOIN Person person ON person.id IN activity.responsibles\s
                        LEFT JOIN Address address ON address.id = activity.address\s
                        LEFT JOIN ServiceCall serviceCall ON serviceCall.id = activity.object.objectId\s
                        LEFT JOIN Equipment equipment ON activity.equipment = equipment.id\s
                        LEFT JOIN BusinessPartner businessPartner ON activity.businessPartner = businessPartner.id\s
                        WHERE person.id IN ('%s')\s
                            AND activity.executionStage != '%s'\s
                            AND activity.executionStage != '%s'\s
                            AND activity.endDateTime > '%s'\s
                            AND activity.startDateTime < '%s'\s
            """.formatted(
            String.join("','", resourceIds.stream().map(UuidMapper::toFsmId).toList()),
            ExecutionStage.CANCELLED.name(),
            ExecutionStage.CLOSED.name(),
            DateTimeService.fromInstantToISOStringNoMilliseconds(filter.getEarliest()),
            DateTimeService.fromInstantToISOStringNoMilliseconds(filter.getLatest())
        );

        if (!filter.getActivitiesToExclude().isEmpty()) {
            sqlSelect += String.format("AND activity.id NOT IN ('%s') ",
                String.join("','", filter.getActivitiesToExclude().stream().map(UuidMapper::toFsmId).toList()));
        }

        sqlSelect += "ORDER BY activity.id ASC";
        return Map.of("query", sqlSelect);
    }

    private Map<UUID, List<Booking>> unwrapPagesByPersonId(List<ActivityAsBookingDto> pages, BookingsFilter filter) {
        var bookingsByPersonId = new HashMap<UUID, List<Booking>>();
        pages.stream()
            .flatMap(activityDto -> activityDto.getData().stream())
            .forEach(activityData -> {
                var personId = activityData.getPerson().id();
                var location = (activityData.getAddress() != null && activityData.getAddress().location() != null) ?
                    new Location(activityData.getAddress().location().latitude(), activityData.getAddress().location().longitude()) : null;
                var activity = activityData.getActivity(); // Activity cannot be null here, unless Query API is broken
                var serviceCall = activityData.getServiceCall();
                var equipment = activityData.getEquipment();
                var businessPartner = activityData.getBusinessPartner();

                BookingJob bookingJob = new BookingJob(
                    UuidMapper.toUUID(activity.id()),
                    serviceCall != null ? UuidMapper.toUUID(serviceCall.id()) : null,
                    activity.lastChanged() != null ? Instant.ofEpochMilli(activity.lastChanged()) : null,
                    location,
                    equipment != null ? UuidMapper.toUUID(equipment.id()) : null, // Ignored if equipment entity is set
                    businessPartner != null ? UuidMapper.toUUID(businessPartner.id()) : null // Ignored if businessPartner entity is set
                );

                bookingJob.setEquipment(equipment != null ? new Entity(UuidMapper.toUUID(equipment.id()),
                    equipment.externalId(),
                    udfValuesToMap(equipment.udfValues())) : null
                );
                bookingJob.setBusinessPartner(businessPartner != null ? new Entity(UuidMapper.toUUID(businessPartner.id()),
                    businessPartner.externalId(),
                    udfValuesToMap(businessPartner.udfValues())) : null
                );

                var exclusive = isBookingExclusive(
                    filter.isConsiderReleasedAsExclusive(),
                    filter.isConsiderPlannedAsExclusive(),
                    ExecutionStage.valueOf(activity.executionStage())
                );

                // Set udf values for booking job
                if (activity.udfValues() != null) {
                    bookingJob.addUdfValues(udfValuesToMap(activity.udfValues()));
                }
                if (serviceCall != null && serviceCall.udfValues() != null) {
                    bookingJob.addUdfValues(udfValuesToMap(serviceCall.udfValues()));
                }

                bookingsByPersonId.computeIfAbsent(UuidMapper.toUUID(personId), k -> new ArrayList<>()).add(
                    new Booking.Builder()
                        .start(DateTimeService.toInstant(activity.startDateTime()))
                        .end(DateTimeService.toInstant(activity.endDateTime()))
                        .location(location)
                        .job(bookingJob)
                        .exclusive(exclusive)
                        .build()
                );
            });
        return bookingsByPersonId;
    }

    private boolean isBookingExclusive(boolean considerReleasedAsExclusive,
                                       boolean considerPlannedAsExclusive,
                                       ExecutionStage executionStage) {
        var exclusive = false;
        if (considerReleasedAsExclusive) {
            exclusive = executionStage == ExecutionStage.EXECUTION;
        }

        if (considerPlannedAsExclusive) {
            exclusive = exclusive || executionStage == ExecutionStage.DISPATCHING;
        }
        return exclusive;
    }
}
