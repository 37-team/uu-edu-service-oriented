package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Holiday;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Resource;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceOptions;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.ResourcesRowMapper;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.holiday.HolidayService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;
import io.micrometer.tracing.annotation.NewSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;

@Service
public class ResourceDataRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String RESOURCE_QUERY = """
        SELECT p.*, array_to_json(array_agg(wtp)) as resource_wtp
        FROM Person p
        LEFT JOIN (
            SELECT pwtp.person,
                   (pwtp.startdate at time zone 'utc') as start,
                   (pwtp.enddate at time zone 'utc') as end,
                   wtp.weeks
            FROM personworktimepattern pwtp,
                 worktimepattern wtp
            WHERE pwtp.worktimepattern = wtp.id
        ) AS wtp ON p.id = wtp.person
        WHERE p.id = ANY (?)
          AND p.plannableresource = 't'
          AND p.inactive = 'f'
        """;

    private final JdbcTemplate jdbcTemplate;

    private final ResourcesRowMapper rowMapper;

    private final BookingsRepository bookingsRepository;

    private final SkillDataRepository skillRepository;

    private final UdfRepository udfRepository;

    private final HolidayService holidayService;

    private final LocationRepository locationRepository;

    private final ActiveTenantProvider activeTenantProvider;

    private final int holidaysFutureTimeoutInMs;

    private final TracingService tracingService;

    public ResourceDataRepository(DataSource dataSource,
                                  BookingsRepository bookingsRepository,
                                  SkillDataRepository skillRepository,
                                  UdfRepository udfRepository,
                                  HolidayService holidayService,
                                  LocationRepository locationRepository,
                                  ActiveTenantProvider activeTenantProvider,
                                  @Value("${service.holiday.futureTimeoutMs:20000}") int holidaysFutureTimeoutInMs,
                                  TracingService tracingService) {
        this.bookingsRepository = bookingsRepository;
        this.skillRepository = skillRepository;
        this.udfRepository = udfRepository;
        this.holidayService = holidayService;
        this.locationRepository = locationRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.tracingService = tracingService;
        rowMapper = new ResourcesRowMapper();
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.holidaysFutureTimeoutInMs = holidaysFutureTimeoutInMs;
    }

    @NewSpan(name = "find-resources\uD83C\uDF54")
    public List<Resource> findResourcesByIds(List<UUID> ids, ResourceOptions resourceOptions, BookingsFilter bookingFilter, FetchRequest fetchUdfRequest, FetchRequest fetchSkillRequest) {

        tracingService.eventOnCurrentSpan("started");

        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();
        Map<String, Set<String>> udfs = fetchUdfRequest.getRequestData();
        Map<String, Set<String>> skills = fetchSkillRequest.getRequestData();

        CompletableFuture<Map<UUID, List<Holiday>>> holidaysFuture = resourceOptions.isHolidaysEnabled() ?
            holidayService.fetchHolidays(tenant, ids, bookingFilter.getEarliest(), bookingFilter.getLatest(), resourceOptions.isCacheHolidays()) :
            CompletableFuture.completedFuture(Collections.emptyMap());

        // ----- fetch resources -----
        var statementProvider = new BaseStatementProvider(tenant)
            .appendWithSingleArg(RESOURCE_QUERY, ids.toArray(UUID[]::new));

        considerResourceOptions(statementProvider, resourceOptions);
        statementProvider.append("GROUP by p.id");

        List<Resource> resources = jdbcTemplate.query(statementProvider, new RowMapperResultSetExtractor<>(rowMapper));

        if (resources == null) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Resources returned null for ids {}", ids.stream().map(UUID::toString).collect(Collectors.joining(", ")));
            }
            return new ArrayList<>();
        }

        List<UUID> foundResourceIds = resources.stream().map(Resource::getId).collect(Collectors.toList());
        if (ids.size() != foundResourceIds.size() && LOG.isWarnEnabled()) {
            LOG.warn("Requested ({}) and found ({}) resource count does not match -> potential delay in indexing.", ids.size(), foundResourceIds.size());
        }

        if (foundResourceIds.isEmpty()) {
            return new ArrayList<>();
        }
        tracingService.eventOnCurrentSpan("resources-fetched");
        tracingService.tagOnCurrentSpan("found-resources", foundResourceIds.size());

        // ----- fetch resource bookings -----
        Map<UUID, List<Booking>> bookingsMap = bookingsRepository.fetchBookings(ids, bookingFilter, tenant, fetchUdfRequest);
        tracingService.eventOnCurrentSpan("bookings-fetched");
        tracingService.tagOnCurrentSpan("total-bookings", bookingsMap.values().stream().mapToInt(List::size).sum());

        // ----- fetch resource skills -----
        var skillPersonFilter = new FetchesFilter(skills.get(FetchesType.RESOURCE.name()), fetchSkillRequest.doNotReturn());
        Map<UUID, List<Skill>> skillsMap = skillRepository.fetchSkills(ids, tenant, skillPersonFilter);
        tracingService.eventOnCurrentSpan("skills-fetched");
        tracingService.tagOnCurrentSpan("total-skills", skillsMap.values().stream().mapToInt(List::size).sum());

        // ----- fetch person udfs -----
        var udfPersonFilter = new FetchesFilter(udfs.get(FetchesType.RESOURCE.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, List<Udf>> personUdfMap = udfRepository.getUdfsByObjectIds(foundResourceIds, tenant, udfPersonFilter, PERSON.getTableName());
        tracingService.eventOnCurrentSpan("person-udfs-fetched");

        // ----- fetch location -----
        Map<UUID, Location> locationMap = locationRepository.fetchLocationsForResources(ids, tenant);
        tracingService.eventOnCurrentSpan("location-fetched");

        // ----- fetch holidays from async call -----
        Map<UUID, List<Holiday>> holidaysMap = fetchHolidaysFromFuture(holidaysFuture);
        tracingService.eventOnCurrentSpan("holidays-fetched");

        // ----- join all resource data and filter -----
        List<Resource> filteredResources = new ArrayList<>();
        for (Resource resource : resources) {
            resource.addUdfs(personUdfMap.get(resource.getId()));
            resource.addSkills(Optional.ofNullable(skillsMap.get(resource.getId())).orElse(Collections.emptyList()));
            resource.addBookings(bookingsMap.get(resource.getId()));
            resource.addHolidays(holidaysMap.get(resource.getId()));
            resource.addHomeLocation(locationMap.get(resource.getId()));
            if (matchesGeoFilter(resource, resourceOptions.isGeocodedOnly())) {
                filteredResources.add(resource);
            }
        }

        return filteredResources;
    }

    private void considerResourceOptions(BaseStatementProvider statementProvider, ResourceOptions resourceOptions) {
        if (resourceOptions.isIncludeCrowdPersons() && !resourceOptions.isIncludeInternalPersons()) {
            statementProvider.append(" AND p.crowdtype IS NOT NULL AND p.crowdtype != 'NON_CROWD'\n");
        } else if (resourceOptions.isIncludeInternalPersons() && !resourceOptions.isIncludeCrowdPersons()) {
            statementProvider.append(" AND (p.crowdtype IS NULL OR p.crowdtype = 'NON_CROWD')\n");
        }
    }

    private boolean matchesGeoFilter(Resource resource, boolean isGeocodedOnly) {
        return !isGeocodedOnly || resource.getHomeLocation() != null;
    }

    private Map<UUID, List<Holiday>> fetchHolidaysFromFuture(CompletableFuture<Map<UUID, List<Holiday>>> holidaysFuture) {
        try {
            return holidaysFuture.get(holidaysFutureTimeoutInMs, TimeUnit.MILLISECONDS);
        } catch (ExecutionException | TimeoutException e) {
            LOG.error("Exception while fetching holidays", e);
        } catch (InterruptedException e) {
            LOG.error("InterruptedException while fetching holidays", e);
            Thread.currentThread().interrupt();
        }
        return Collections.emptyMap();
    }
}
