package com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Activity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.AdditionalDataOptionsRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.InvalidJob;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Job;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobIdsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ServiceCall;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ValidatedJobs;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.JobValidationService;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.partnerdispatch.PartnerDispatchService;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job.ActivityAndRequirementClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job.ActivityClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job.ActivityQueryApi;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.requirements.RequirementClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.springboot.starter.featureflag.FeatureFlagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class JobProxyService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String FEATURE_FLAG_SKILL_PROFICIENCIES = "cloud-ai-read-model-fetch-skills-from-microservice-fsmcpb-143344";

    private final ActivityClient activityClient;

    private final RequirementClient requirementClient;

    private final ActivityAndRequirementClient activityAndRequirementClient;

    private final PartnerDispatchService partnerDispatchService;

    private final ActiveTenantProvider activeTenantProvider;

    private final JobValidationService jobValidationService = new JobValidationService();

    private final FeatureFlagService featureFlagService;

    public JobProxyService(ActivityClient activityClient,
                           RequirementClient requirementClient, ActivityAndRequirementClient activityAndRequirementClient,
                           PartnerDispatchService partnerDispatchService,
                           ActiveTenantProvider activeTenantProvider,
                           FeatureFlagService featureFlagService) {
        this.activityClient = activityClient;
        this.requirementClient = requirementClient;
        this.activityAndRequirementClient = activityAndRequirementClient;
        this.partnerDispatchService = partnerDispatchService;
        this.activeTenantProvider = activeTenantProvider;
        this.featureFlagService = featureFlagService;
    }

    public ValidatedJobs getJobs(List<UUID> jobsIds, AdditionalDataOptionsRequest additionalOptions, FetchRequest fetchUdfRequest) {
        final String ERROR_MESSAGE = "Unexpected Error while fetching jobs in proxy mode.";
        var udfListFilter = new UdfMapFilter(fetchUdfRequest);

        try {
            var currentRequestContext = new ReadModelRequestContext();
            var tenant = activeTenantProvider.resolveCurrentTenantIdentifier();
            var futureActivities = activityClient.queryActivitiesByActivityIds(currentRequestContext, UuidMapper.toFsmIds(jobsIds));
            var enableSkillProficiencies = featureFlagService.getBooleanFlag(FEATURE_FLAG_SKILL_PROFICIENCIES, false);
            var futureRequirementsFromSkillMicroservice = CompletableFuture.completedFuture(Collections.emptyMap());
            if (enableSkillProficiencies) {
                // TODO FSMCPB-91042 - fetch requirements with skill proficiencies from skill microservice
                futureRequirementsFromSkillMicroservice = CompletableFuture.completedFuture(Collections.emptyMap());
            }
            var futureRequirements = requirementClient.queryRequirementsByActivityIdsAsync(currentRequestContext, UuidMapper.toFsmIds(jobsIds));
            var futureResourceExcludeList = partnerDispatchService.fetchResourceExcludeList(jobsIds, tenant, additionalOptions.isUseExcludeList());
            var allFutures = CompletableFuture.allOf(futureActivities, futureRequirements, futureRequirementsFromSkillMicroservice, futureResourceExcludeList);
            allFutures.get();

            List<Job> validJobs = new ArrayList<>();
            List<InvalidJob> invalidJobs = new ArrayList<>();
            var requirements = futureRequirements.get();
            var requirementsFromSkillMicroservice = futureRequirementsFromSkillMicroservice.get();
            var excludeListPerJob = partnerDispatchService.getExcludeMapFromFutureWithFallbackToEmptyMap(futureResourceExcludeList);
            for (ActivityQueryApi activity : futureActivities.get()) {

                var activityForJob = new Activity(
                    activity.id(),
                    activity.activityExternalId(),
                    udfListFilter.filterUDFs(FetchesType.JOB, activity.udfValues()), activity.lastChanged(),
                    activity.earliestStartDateTime(),
                    activity.dueDateTime(),
                    activity.travelTimeToInMinutes(),
                    activity.travelTimeFromInMinutes(),
                    activity.executionStage(),
                    activity.durationInMinutes(),
                    activity.syncStatus(),
                    activity.responsibles(),
                    activity.startDateTime(),
                    activity.endDateTime(),
                    activity.plannedDurationInMinutes()
                );


                var job = new Job(activityForJob);

                // TODO FSMCPB-91042 -Compare and use the results from both skill clients while logging any differences
                var requirementsForJob = requirements.get(job.getId());
                var requirementsFromSkillMicroserviceForJob = requirementsFromSkillMicroservice.get(job.getId());
                //  TODO FSMCPB-91042 - use correct requirements
                job.addRequirements(requirementsForJob);

                job.setEquipment(new Entity(activity.equipmentId(), activity.equipmentExternalId(), udfListFilter.filterUDFs(FetchesType.EQUIPMENT, activity.equipmentUdfValues())));
                job.setBusinessPartner(new Entity(activity.businessPartnerId(), activity.businessPartnerExternalId(), udfListFilter.filterUDFs(FetchesType.BUSINESSPARTNER, activity.businessPartnerUdfValues())));
                job.setServiceCall(new ServiceCall(activity.serviceCallId(), activity.serviceCallExternalId(), udfListFilter.filterUDFs(FetchesType.JOB, activity.serviceCallUdfValues()), activity.priority()));
                job.setLocation(activity.location());
                job.setResourceExcludeList(excludeListPerJob.get(job.getId()));

                var validationResult = jobValidationService.validateOptimizationJob(job);
                if (validationResult.isValid()) {
                    validJobs.add(job);
                } else {
                    invalidJobs.add(new InvalidJob(job.getId(), job.getActivity().getLastChanged(), validationResult.getReason()));
                }
            }

            return new ValidatedJobs(validJobs, invalidJobs);
        } catch (ExecutionException e) {
            LOG.error(ERROR_MESSAGE, e);
            throw new DomainException(ERROR_MESSAGE);
        } catch (InterruptedException e) {
            LOG.error(ERROR_MESSAGE, e);

            Thread.currentThread().interrupt();
            throw new DomainResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
    }

    public Map<String, List<String>> filterAndPartitionJobIdsByRequirements(JobIdsFilter filter) {
        return activityAndRequirementClient.filterAndPartitionJobsByIdsAndRequirements(new ReadModelRequestContext(), filter);
    }
}
