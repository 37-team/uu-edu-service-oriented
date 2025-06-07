package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ExecutionStage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.BusinessPartner;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Equipment;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.LocationDto;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue.udfValuesToMap;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ACTIVITY;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ADDRESS;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.BUSINESSPARTNER;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.EQUIPMENT;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SERVICECALL;
import static java.util.Objects.nonNull;

@Component
public class ActivityClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(ACTIVITY, ADDRESS, SERVICECALL, EQUIPMENT, BUSINESSPARTNER);

    public ActivityClient(@Value("${service.query-api.host}") String queryApiHost,
                          @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<List<ActivityQueryApi>> queryActivitiesByActivityIds(ReadModelRequestContext requestContext, List<String> activityIds) {
        var body = createQueryBody(activityIds);
        List<ActivityQueryApiDto> pages = getAllPages(requestContext, REQUEST_DTOS, body, ActivityQueryApiDto.class);
        return CompletableFuture.completedFuture(unwrapPages(pages));
    }

    private List<ActivityQueryApi> unwrapPages(List<ActivityQueryApiDto> pages) {
        return pages.stream()
            .flatMap(activityDto -> activityDto.getData().stream())
            .map(activityData -> {
                var activity = activityData.getActivity();
                var location = activityData.getAddress() != null ? activityData.getAddress().getLocation() : null;
                var serviceCall = activityData.getServiceCall();
                var equipment = activityData.getEquipment();
                var businessPartner = activityData.getBusinessPartner();

                return createActivityQueryApi(activity, location, serviceCall, equipment, businessPartner);
            })
            .toList();
    }

    private Map<String, String> createQueryBody(List<String> activityIds) {
        String sqlSelect = """
            SELECT\s
                activity.id,\s
                activity.externalId,\s
                activity.lastChanged,\s
                activity.earliestStartDateTime,\s
                activity.dueDateTime,\s
                activity.startDateTime,\s
                activity.executionStage,\s
                activity.endDateTime,\s
                activity.responsibles,\s
                activity.syncStatus,\s
                activity.durationInMinutes,\s
                activity.plannedDurationInMinutes,\s
                activity.udfValues,\s
                activity.travelTimeToInMinutes,\s
                activity.travelTimeFromInMinutes,\s
                address.location,\s
                serviceCall.id,\s
                serviceCall.externalId,\s
                serviceCall.priority,\s
                serviceCall.udfValues,\s
                equipment.id,\s
                equipment.externalId,\s
                equipment.udfValues,\s
                businessPartner.id,\s
                businessPartner.externalId,\s
                businessPartner.udfValues\s
             FROM\s
                Activity activity\s
                LEFT JOIN\s
                  Address address\s
                ON\s
                  address.id = activity.address\s
                LEFT JOIN\s
                  ServiceCall serviceCall\s
                ON\s
                  serviceCall.id = activity.object.objectId\s
                LEFT JOIN\s
                  Equipment equipment\s
                ON\s
                  activity.equipment = equipment.id\s
                LEFT JOIN BusinessPartner businessPartner\s
                ON activity.businessPartner = businessPartner.id\s
             WHERE activity.id IN ('%s')\s
             ORDER BY activity.id ASC
            """.formatted(String.join("','", activityIds));
        return Map.of("query", sqlSelect);
    }

    private ActivityQueryApi createActivityQueryApi(ActivityQueryApiDto.ActivityDto activity,
                                                    LocationDto location,
                                                    ActivityQueryApiDto.ServiceCallDto serviceCall,
                                                    Equipment equipment,
                                                    BusinessPartner businessPartner) {
        return new ActivityQueryApi(
            UuidMapper.toUUID(activity.getId()),
            nonNull(activity.getExternalId()) ? activity.getExternalId() : null,
            nonNull(serviceCall) ? UuidMapper.toUUID(serviceCall.getId()) : null,
            nonNull(serviceCall) ? serviceCall.getExternalId() : null,
            nonNull(equipment) ? UuidMapper.toUUID(equipment.id()) : null,
            nonNull(equipment) ? equipment.externalId() : null,
            nonNull(businessPartner) ? UuidMapper.toUUID(businessPartner.id()) : null,
            nonNull(businessPartner) ? businessPartner.externalId() : null,
            Instant.ofEpochMilli(activity.getLastChanged()),
            DateTimeService.toInstant(activity.getEarliestStartDateTime()),
            DateTimeService.toInstant(activity.getDueDateTime()),
            nonNull(location) ? new Location(location.latitude(), location.longitude()) : null,
            nonNull(activity.getDurationInMinutes()) ? activity.getDurationInMinutes() : null,
            DateTimeService.toInstant(activity.getStartDateTime()),
            DateTimeService.toInstant(activity.getEndDateTime()),
            nonNull(activity.getPlannedDurationInMinutes()) ? activity.getPlannedDurationInMinutes() : null,
            nonNull(activity.getTravelTimeToInMinutes()) ? activity.getTravelTimeToInMinutes() : null,
            nonNull(activity.getTravelTimeFromInMinutes()) ? activity.getTravelTimeFromInMinutes() : null,
            nonNull(activity.getExecutionStage()) ? ExecutionStage.valueOf(activity.getExecutionStage()) : null,
            activity.getResponsibles().stream().map(UuidMapper::toUUID).toList(),
            udfValuesToMap(activity.getUdfValues()),
            udfValuesToMap(nonNull(equipment) ? equipment.udfValues() : null),
            udfValuesToMap(nonNull(businessPartner) ? businessPartner.udfValues() : null),
            udfValuesToMap(nonNull(serviceCall) ? serviceCall.getUdfValues() : null),
            activity.getSyncStatus(),
            nonNull(serviceCall) ? serviceCall.getPriority() : null
        );
    }
}
