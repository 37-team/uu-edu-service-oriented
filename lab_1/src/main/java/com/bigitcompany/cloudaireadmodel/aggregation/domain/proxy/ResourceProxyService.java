package com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourcePartitionRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Holiday;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.PersonsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Resource;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceIdsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceOptions;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.holiday.HolidayService;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking.ActivityAsBookingClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking.PersonReservationsBookingClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking.WorkTimeBookingClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.location.LocationClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource.PartitionsClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource.PersonClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource.PersonQueryApi;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource.ResourceIdsClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.skill.SkillClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.skill.SkillMicroserviceClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.worktimepattern.WorktimePatternClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.springboot.starter.featureflag.FeatureFlagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ResourceProxyService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ERROR_MESSAGE = "Unexpected Error while fetching resources in proxy mode.";

    private static final String FEATURE_FLAG_SKILL_PROFICIENCIES = "cloud-ai-read-model-fetch-skills-from-microservice-fsmcpb-143344";

    private final ResourceIdsClient resourceIdsClient;

    private final PartitionsClient partitionsClient;

    private final PersonClient personClient;

    private final WorktimePatternClient worktimePatternClient;

    private final PersonReservationsBookingClient bookingClient;

    private final WorkTimeBookingClient workTimeBookingClient;

    private final ActivityAsBookingClient activityAsBookingClient;

    private final HolidayService holidayService;

    private final ActiveTenantProvider activeTenantProvider;

    private final SkillClient skillClient;

    private final SkillMicroserviceClient skillMicroserviceClient;

    private final LocationClient locationClient;

    private final FeatureFlagService featureFlagService;

    public ResourceProxyService(ResourceIdsClient resourceIdsClient,
                                PersonClient personClient,
                                PartitionsClient partitionsClient,
                                WorktimePatternClient worktimePatternClient,
                                PersonReservationsBookingClient bookingClient,
                                ActivityAsBookingClient activityAsBookingClient,
                                WorkTimeBookingClient workTimeBookingClient,
                                HolidayService holidayService,
                                ActiveTenantProvider activeTenantProvider,
                                SkillClient skillClient,
                                SkillMicroserviceClient skillMicroserviceClient,
                                LocationClient locationClient,
                                FeatureFlagService featureFlagService) {
        this.resourceIdsClient = resourceIdsClient;
        this.partitionsClient = partitionsClient;
        this.personClient = personClient;
        this.worktimePatternClient = worktimePatternClient;
        this.bookingClient = bookingClient;
        this.activityAsBookingClient = activityAsBookingClient;
        this.workTimeBookingClient = workTimeBookingClient;
        this.holidayService = holidayService;
        this.activeTenantProvider = activeTenantProvider;
        this.skillClient = skillClient;
        this.skillMicroserviceClient = skillMicroserviceClient;
        this.locationClient = locationClient;
        this.featureFlagService = featureFlagService;
    }

    public List<UUID> filterResourceIds(ResourceIdsFilter filter) {
        return resourceIdsClient.filterResourcesBySkills(new ReadModelRequestContext(), filter);
    }

    public List<Resource> getResources(List<UUID> personIds, ResourceOptions options, BookingsFilter bookingFilter, FetchRequest fetchUdfRequest, FetchRequest fetchSkillRequest, int limit) {

        var personFilter = new PersonsFilter(UuidMapper.toFsmIds(personIds), options.isIncludeInternalPersons(), options.isIncludeCrowdPersons());
        var udfListFilter = new UdfMapFilter(fetchUdfRequest);
        var holidayEnabled = options.isHolidaysEnabled();

        var currentRequestContext = new ReadModelRequestContext();
        var tenant = activeTenantProvider.resolveCurrentTenantIdentifier();

        var persons = personClient.queryPersonsSync(currentRequestContext, personFilter, limit);
        List<Resource> resources = new ArrayList<>();

        var filteredPersonIds = persons.stream().map(PersonQueryApi::id).toList();
        if (filteredPersonIds.isEmpty()) {
            return resources;
        }

        try {
            var workTimePatternsFuture = worktimePatternClient.queryWorkTimePatternsByPersonIds(currentRequestContext, filteredPersonIds);
            var personReservationsAsBookingsFuture = bookingClient.queryPersonReservationsAsBookings(currentRequestContext, filteredPersonIds, bookingFilter);
            var activitiesAsBookingsFuture = activityAsBookingClient.queryActivityAsBookingByPersonIds(currentRequestContext, filteredPersonIds, bookingFilter);
            var workTimesAsBookingsFuture = workTimeBookingClient.queryWorkTimesAsBookings(currentRequestContext, filteredPersonIds, bookingFilter);
            CompletableFuture<Map<UUID, List<Holiday>>> holidaysFuture = holidayEnabled ?
                holidayService.fetchHolidays(tenant, filteredPersonIds, bookingFilter.getEarliest(), bookingFilter.getLatest(), options.isCacheHolidays()) :
                CompletableFuture.completedFuture(Collections.emptyMap());
            var skillsByPersonIdsFutureWithoutProficiencies = skillClient.querySkillsByPersonIdsAsync(currentRequestContext, filteredPersonIds, fetchSkillRequest);
            var enableSkillProficiencies = featureFlagService.getBooleanFlag(FEATURE_FLAG_SKILL_PROFICIENCIES, false);
            var skillsByPersonIdsFuture = skillsByPersonIdsFutureWithoutProficiencies;

            if (enableSkillProficiencies) {
                //TODO FSMCPB-91042: fetch all data from Skill microservice
                skillsByPersonIdsFuture = skillMicroserviceClient.fetchSkillsWithProficienciesAsync(skillsByPersonIdsFutureWithoutProficiencies.get());
            }
            var locationByPersonIdsFuture = locationClient.queryLocationsByPersonIds(currentRequestContext, filteredPersonIds);

            // Wait until all async calls are finished
            var allFutures = CompletableFuture.allOf(
                workTimePatternsFuture,
                personReservationsAsBookingsFuture,
                activitiesAsBookingsFuture,
                workTimesAsBookingsFuture,
                holidaysFuture,
                skillsByPersonIdsFuture,
                locationByPersonIdsFuture
            );
            allFutures.get();
            var workTimePatternsByPersonId = workTimePatternsFuture.get();
            var personReservationAsBookingsByPersonId = personReservationsAsBookingsFuture.get();
            var activitiesAsBookingsByPersonId = activitiesAsBookingsFuture.get();
            var workTimesBookingMap = workTimesAsBookingsFuture.get();
            var holidaysByPersonId = holidaysFuture.get();
            var skillsByPersonIds = skillsByPersonIdsFuture.get();
            var locationByPersonIds = locationByPersonIdsFuture.get();

            // Build resources from all data sources
            for (PersonQueryApi person : persons) {
                List<WorkTimePattern> workTimePattern = Optional.ofNullable(workTimePatternsByPersonId.get(person.id())).orElse(Collections.emptyList());
                if (workTimePattern.isEmpty() && LOG.isWarnEnabled()) {
                    LOG.warn("[Unexpected Data] No work time pattern found for person with id: {}. QueryApi returned WTPs for the following persons: {}",
                        person.id(), String.join(", ", workTimePatternsByPersonId.keySet().stream().map(UUID::toString).toList()));
                }

                // TODO FSMCPB-91042 - Compare and use the results from both skill clients while logging any differences
                var skillsForPerson = Optional.ofNullable(skillsByPersonIds.get(person.id())).orElse(Collections.emptyList());

                var resource = new Resource.Builder()
                    .id(person.id())
                    .lastChanged(Date.from(Instant.now())) //mock
                    .origin(person.crowdType())
                    .externalId(person.personExternalId())
                    .realTimeLocation(person.location())
                    .locationLastUserChangedDate(person.locationLastUserChangedDate() != null ? Date.from(person.locationLastUserChangedDate()) : null)
                    .udfValues(udfListFilter.filterUDFs(FetchesType.RESOURCE, person.udfValues()))
                    .skills(skillsForPerson)  // TODO FSMCPB-91042 - use correct skills
                    .worktimePattern(workTimePattern)
                    .build();

                resource.addBookings(personReservationAsBookingsByPersonId.get(person.id()));
                resource.addBookings(activitiesAsBookingsByPersonId.get(person.id()));
                resource.addBookings(workTimesBookingMap.get(person.id()));
                resource.addHolidays(holidaysByPersonId.get(person.id()));
                resource.addHomeLocation(locationByPersonIds.get(resource.getId()));

                resources.add(resource);
            }
        } catch (ExecutionException e) {
            LOG.error(ERROR_MESSAGE, e);
            throw new DomainException(ERROR_MESSAGE);
        } catch (InterruptedException e) {
            LOG.error(ERROR_MESSAGE, e);

            Thread.currentThread().interrupt();
            throw new DomainResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_MESSAGE);
        }
        return resources;
    }

    public Map<String, List<String>> getPartitions(ResourcePartitionRequestDto resourcePartitionRequestDto) {
        return partitionsClient.fetchPersonsAndSkills(new ReadModelRequestContext(), resourcePartitionRequestDto);
    }
}
