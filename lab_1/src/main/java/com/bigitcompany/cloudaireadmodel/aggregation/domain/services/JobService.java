package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Activity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.AdditionalDataOptionsRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.InvalidJob;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Job;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobIdsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobValidationResult;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Requirement;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ServiceCall;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ValidatedJobs;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.ActivityDatabaseRepository;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.JobIdsFilterRepository;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.RequirementDataRepository;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.partnerdispatch.PartnerDispatchService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JobService {

    private final ActivityDatabaseRepository activityDatabaseRepository;

    private final JobIdsFilterRepository jobIdsFilterRepository;

    private final PartnerDispatchService partnerDispatchService;

    private final ActiveTenantProvider activeTenantProvider;

    private final RequirementDataRepository requirementsRepository;

    private final JobValidationService jobValidationService;

    private final TracingService tracingService;

    public JobService(ActivityDatabaseRepository activityDatabaseRepository,
                      JobIdsFilterRepository jobIdsFilterRepository,
                      PartnerDispatchService partnerDispatchService,
                      ActiveTenantProvider activeTenantProvider,
                      RequirementDataRepository requirementsRepository,
                      TracingService tracingService) {
        this.activityDatabaseRepository = activityDatabaseRepository;
        this.jobIdsFilterRepository = jobIdsFilterRepository;
        this.partnerDispatchService = partnerDispatchService;
        this.activeTenantProvider = activeTenantProvider;
        this.requirementsRepository = requirementsRepository;
        this.tracingService = tracingService;
        jobValidationService = new JobValidationService();
    }


    private Map<UUID, List<Requirement>> fetchRequirements(List<UUID> activityIds, String tenant) {
        var requirementsMap = requirementsRepository.getByActivityIds(activityIds, tenant);
        tracingService.eventOnCurrentSpan("requirements-fetched");
        tracingService.tagOnCurrentSpan("total-requirements-found", requirementsMap.values().stream().mapToInt(List::size).sum());
        return requirementsMap;
    }

    private Map<String, String> makeUdfValues(List<Udf> udfs) {
        if (udfs == null || udfs.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> udfValues = new HashMap<>();
        udfs.stream().filter(
                udf -> udf.getName() != null
        ).forEach(udf -> udfValues.put(udf.getName(), udf.getValue()));
        return udfValues;
    }

    public ValidatedJobs getJobs(List<UUID> jobsIds, AdditionalDataOptionsRequest additionalOptions, FetchRequest fetchUdfRequest) {
        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();

        var futureResourceExcludeList = partnerDispatchService.fetchResourceExcludeList(jobsIds, tenant, additionalOptions.isUseExcludeList());

        var jobs = activityDatabaseRepository.findActivitiesByIds(jobsIds, fetchUdfRequest);
        var requirementsMap = fetchRequirements(jobsIds, tenant);
        var resourceExcludeList = partnerDispatchService.getExcludeMapFromFutureWithFallbackToEmptyMap(futureResourceExcludeList);

        List<Job> validJobs = new ArrayList<>();
        List<InvalidJob> invalidJobs = new ArrayList<>();
        jobs.forEach(activityDatabaseValues -> {

            var activityData = activityDatabaseValues.getActivityData();
            var activity = new Activity(
                    activityData.getId(),
                    activityData.getExternalId(),
                    makeUdfValues(activityDatabaseValues.getActivityUDFs()),
                    activityData.getLastChanged(),
                    activityData.getEarliestStart(),
                    activityData.getDue(),
                    activityData.getTravelTimeToInMinutes(),
                    activityData.getTravelTimeFromInMinutes(),
                    activityData.getExecutionStage(),
                    activityData.getDurationMinutes(),
                    activityData.getSyncStatus(),
                    activityData.getResponsibles(),
                    activityData.getStart(),
                    activityData.getEnd(),
                    activityData.getPlannedDurationInMinutes()
            );

            var job = new Job(activity);
            job.setServiceCall(new ServiceCall(
                    activityData.getServiceCallId(),
                    activityData.getServiceCallExternalId(),
                    makeUdfValues(activityDatabaseValues.getServiceCallUDFs()),
                    activityData.getPriority()
                )
            );
            var businessPartnerId = activityData.getBusinessPartnerId();
            var equipmentId = activityData.getEquipmentId();

            job.setBusinessPartner(
                businessPartnerId == null
                    ? null
                    : new Entity(businessPartnerId,
                    activityData.getBusinessPartnerExternalId(),
                    activityDatabaseValues.getBusinessPartnerUdfMap().get(businessPartnerId)));

            job.setEquipment(
                equipmentId == null
                    ? null
                    : new Entity(equipmentId,
                    activityData.getEquipmentExternalId(),
                    activityDatabaseValues.getEquipmentUdfMap().get(equipmentId)));

            job.setResourceExcludeList(resourceExcludeList.get(job.getId()));
            job.addRequirements(requirementsMap.get(job.getId()));
            job.setLocation(activityDatabaseValues.getActivityData().getLocation());
            JobValidationResult result = jobValidationService.validateOptimizationJob(job);

            if (result.isValid()) {
                validJobs.add(job);
            } else {
                invalidJobs.add(new InvalidJob(job.getId(), job.getActivity().getLastChanged(), result.getReason()));
            }
        });

        return new ValidatedJobs(validJobs, invalidJobs);
    }

    public Map<String, List<String>> filterAndPartitionJobIdsByRequirements(JobIdsFilter filter) {
        return jobIdsFilterRepository.filterAndPartitionJobIdsByRequirement(filter);
    }
}
