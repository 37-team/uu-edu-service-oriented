package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.model.ActivityData;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.model.ActivityDatabaseValues;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.ActivityDataRowMapper;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ACTIVITY;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.BUSINESSPARTNER;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.EQUIPMENT;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SERVICECALL;

@Component
public class ActivityDatabaseRepository {

    private static final Logger logger = LoggerFactory.getLogger(ActivityDatabaseRepository.class);

    private static final String FETCH_JOBS_DATA_QUERY = """
        SELECT
          activity.id as activity_id,
          activity.externalId as activity_external_id,
          activity.lastchanged as activity_lastchanged,
          activity.earlieststartdatetime as activity_earlieststartdatetime,
          activity.duedatetime as activity_duedatetime,
          activity.startdatetime as activity_startdatetime,
          activity.executionstage as activity_executionstage,
          activity.enddatetime as activity_enddatetime,
          activity.responsibles as activity_responsibles,
          activity.syncstatus as activity_syncstatus,
          activity.durationinminutes as activity_durationinminutes,
          activity.planneddurationinminutes as activity_planneddurationinminutes,
          activity.udfvalues as activity_udfvalues,
          activity.address as activity_address,
          activity.servicecall as activity_servicecall,
          servicecall.priority as servicecall_priority,
          servicecall.externalId as servicecall_external_id,
          activity.equipment as activity_equipment,
          activity.businesspartner as activity_businesspartner,
          address.id as address_id,
          address.location as address_location,
          activity.travel_time_to_in_minutes as activity_travel_time_to_in_minutes,
          activity.travel_time_from_in_minutes as activity_travel_time_from_in_minutes,
          equipment.externalId as equipment_external_id,
          businesspartner.externalId as businesspartner_external_id 
        FROM activity
            LEFT OUTER JOIN address ON activity.address = address.id
            LEFT OUTER JOIN servicecall ON activity.servicecall = servicecall.id
            LEFT OUTER JOIN equipment ON activity.equipment = equipment.id
            LEFT OUTER JOIN businesspartner ON activity.businesspartner = businesspartner.id
        WHERE activity.id = ANY (?)
        ORDER BY activity.id ASC""";

    private final JdbcTemplate jdbcTemplate;

    private final ActivityDataRowMapper activityDataRowMapper;

    private final UdfRepository udfRepository;

    private final EntityDataRepository entityRepository;

    private final ActiveTenantProvider activeTenantProvider;

    @Autowired
    private TracingService tracingService;

    public ActivityDatabaseRepository(ActiveTenantProvider activeTenantProvider,
                              JdbcTemplate jdbcTemplate,
                              UdfRepository udfRepository,
                              EntityDataRepository entityRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.activeTenantProvider = activeTenantProvider;
        this.udfRepository = udfRepository;
        this.entityRepository = entityRepository;
        activityDataRowMapper = new ActivityDataRowMapper();
    }

    @NewSpan("find-jobs-by-ids\uD83C\uDF2D")
    public List<ActivityDatabaseValues> findActivitiesByIds(List<UUID> jobsIds,
                                                            FetchRequest fetchUdfRequest) {

        tracingService.tagOnCurrentSpan("total-jobs", String.valueOf(jobsIds.size()));
        tracingService.eventOnCurrentSpan("started");
        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();
        Map<String, Set<String>> udfs = fetchUdfRequest.getRequestData();

        UUID[] jobsIdsArray = jobsIds.toArray(UUID[]::new);

        List<ActivityData> baseActivityList = fetchActivityDataList(jobsIdsArray, tenant);
        tracingService.eventOnCurrentSpan("job-list-fetched");
        tracingService.tagOnCurrentSpan("total-jobs-found", String.valueOf(baseActivityList != null ? baseActivityList.size() : 0));

        if (baseActivityList == null) {
            var jobIds = Arrays.toString(jobsIdsArray);
            logger.warn("fetchJobList returned null for jobsIdsArray {}", jobIds);
            return new ArrayList<>();
        }

        List<UUID> foundJobIds = baseActivityList.stream().map(ActivityData::getId).toList();

        if (jobsIdsArray.length != foundJobIds.size()) {
            logger.warn("Requested ({}) and found ({}) job count does not match, potential delay in indexing", jobsIdsArray.length, foundJobIds.size());
        }

        if (foundJobIds.isEmpty()) {
            return new ArrayList<>();
        }


        // ----- fetch activity UDFs -----
        var udfActivityFilter = new FetchesFilter(udfs.get(FetchesType.JOB.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, List<Udf>> activityUdfMap = udfRepository.getUdfsByObjectIds(foundJobIds, tenant, udfActivityFilter, ACTIVITY.getTableName());
        tracingService.eventOnCurrentSpan("activity-udfs-fetched");


        // ----- fetch service call UDFs -----
        List<UUID> bookingServiceCallIds = baseActivityList.stream()
            .map(ActivityData::getServiceCallId)
            .filter(Objects::nonNull)
            .toList();
        var udfServiceCallFilter = new FetchesFilter(udfs.get(FetchesType.JOB.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, List<Udf>> serviceCallUdfMap = udfRepository.getUdfsByObjectIds(bookingServiceCallIds, tenant, udfServiceCallFilter, SERVICECALL.getTableName());
        tracingService.eventOnCurrentSpan("service-call-udfs-fetched");

        // ----- fetch equipment -----
        List<UUID> equipmentIds = baseActivityList.stream()
            .map(ActivityData::getEquipmentId)
            .filter(Objects::nonNull)
            .toList();

        var udfEquipmentFilter = new FetchesFilter(udfs.get(FetchesType.EQUIPMENT.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, Map<String, String>> equipmentUdfMap = entityRepository.getEntityByEntityIds(equipmentIds, tenant, udfEquipmentFilter, EQUIPMENT.getTableName());
        tracingService.eventOnCurrentSpan("equipment-fetched");

        // ----- fetch business partner -----
        List<UUID> businessPartnerIds = baseActivityList.stream()
            .map(ActivityData::getBusinessPartnerId)
            .filter(Objects::nonNull)
            .toList();

        var udfBusinessPartnerFilter = new FetchesFilter(udfs.get(FetchesType.BUSINESSPARTNER.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, Map<String, String>> businessPartnerUdfMap = entityRepository.getEntityByEntityIds(businessPartnerIds, tenant, udfBusinessPartnerFilter, BUSINESSPARTNER.getTableName());
        tracingService.eventOnCurrentSpan("business-partner-fetched");


        List<ActivityDatabaseValues> jobsList = new ArrayList<>();
        for (ActivityData activityData : baseActivityList) {
            jobsList.add(
                    new ActivityDatabaseValues(
                            activityData,
                            equipmentUdfMap,
                            businessPartnerUdfMap,
                            serviceCallUdfMap.get(activityData.getServiceCallId()),
                            activityUdfMap.get(activityData.getId())
                    )
            );
        }

        return jobsList;
    }


    @Nullable
    private List<ActivityData> fetchActivityDataList(UUID[] jobsIds, String tenant) {
        PreparedStatementCreator statementCreator = new TenantSqlPreparedStatementProvider(FETCH_JOBS_DATA_QUERY, tenant).setArg(jobsIds);
        return jdbcTemplate.query(statementCreator, new RowMapperResultSetExtractor<>(activityDataRowMapper));
    }
}
