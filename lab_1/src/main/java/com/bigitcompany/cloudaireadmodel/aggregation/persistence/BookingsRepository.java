package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.BookingsResultSetExtractor;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.BookingsResultSetExtractor.JSON_ROW_IDENTIFIER;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ACTIVITY;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.BUSINESSPARTNER;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.EQUIPMENT;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SERVICECALL;

@Component
public class BookingsRepository {

    private final JdbcTemplate jdbcTemplate;

    private final ResultSetExtractor<Map<UUID, List<Booking>>> bookingsResultSetExtractor;

    private final UdfRepository udfRepository;

    private final EntityDataRepository entityRepository;

    private static final String WORKTIME_AS_BOOKINGS_QUERY = "" +
        "SELECT wt.person as person,\n" +
        "'work_time' as booking_type,\n" +
        "wt.startdatetime as start,\n" +
        "wt.enddatetime as end,\n" +
        "'true'::boolean as exclusive,\n" +
        "'{}'::jsonb as location,\n" +
        "'{}'::json as " + JSON_ROW_IDENTIFIER + " \n" +
        "FROM worktime wt \n" +
        "WHERE wt.person = ANY (?)\n";

    private static final String PERSON_RESERVATION_AS_BOOKINGS_QUERY = "" +
        "SELECT p.id as person,\n" +
        "'person_reservation' as booking_type,\n" +
        "per.startdate as start,\n" +
        "per.enddate as end,\n" +
        "per.exclusive as exclusive,\n" +
        "add.location as location,\n" +
        "'{}'::json as " + JSON_ROW_IDENTIFIER + " \n" +
        "FROM person p\n" +
        "INNER JOIN personreservation per ON p.id = any(per.persons)\n" +
        "LEFT JOIN address add ON add.id = per.address\n" +
        "WHERE p.id = ANY(?) \n";

    private static final String ACTIVITY_AS_BOOKINGS_QUERY = "" +
        "SELECT act.person,\n" +
        "       'activity'             as booking_type,\n" +
        "       activity_startdatetime as start,\n" +
        "       activity_enddatetime   as end,\n" +
        "       CASE\n" +
        "           WHEN activity_executionstage = 'EXECUTION' AND ? THEN true\n" +
        "           WHEN activity_executionstage = 'DISPATCHING' AND ? THEN true\n" +
        "           ELSE false\n" +
        "           END                as exclusive,\n" +
        "       act.location           as location,\n" +
        "       row_to_json(act)       as " + JSON_ROW_IDENTIFIER + " \n" +
        "FROM (\n" +
        "         SELECT p.id                                 as person,\n" +
        "                a.id                                 as activity_id,\n" +
        "                a.servicecall                        as service_call_id,\n" +
        "                a.businesspartner                    as activity_business_partner_id,\n" +
        "                (a.lastchanged at time zone 'utc')   as activity_lastchanged,\n" +
        "                (a.startdatetime at time zone 'utc') as activity_startdatetime,\n" +
        "                (a.enddatetime at time zone 'utc')   as activity_enddatetime,\n" +
        "                a.executionstage                     as activity_executionstage,\n" +
        "                add.location                         as location,\n" +
        "                s.id                                 as sc_id,\n" +
        "                s.businesspartner                    as service_call_business_partner_id,\n" +
        "                e.id                                 as e_id\n" +
        "         FROM activity a\n" +
        "                  LEFT JOIN person p on p.id = ANY (a.responsibles)\n" +
        "                  LEFT JOIN address add on a.address = add.id\n" +
        "                  LEFT JOIN servicecall s on s.id = a.servicecall\n" +
        "                  LEFT JOIN equipment e on e.id = a.equipment\n" +
        "         WHERE a.responsibles && (?)\n" +
        "           AND a.enddatetime IS NOT NULL\n" +
        "           AND a.startdatetime IS NOT NULL\n" +
        "           AND a.enddatetime > ?\n" +
        "           AND a.startdatetime < ?\n" +
        "           AND a.executionstage NOT IN ('CLOSED', 'CANCELLED')\n" +
        "     ) as act \n";

    private final TracingService tracingService;

    public BookingsRepository(JdbcTemplate jdbcTemplate, UdfRepository udfRepository, EntityDataRepository entityRepository, TracingService tracingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.udfRepository = udfRepository;
        this.entityRepository = entityRepository;
        this.tracingService = tracingService;
        bookingsResultSetExtractor = new BookingsResultSetExtractor();
    }

    @NewSpan("fetch-bookings\uD83C\uDF70")
    public Map<UUID, List<Booking>> fetchBookings(List<UUID> personIds, BookingsFilter bookingsFilter, String tenant, FetchRequest fetchUdfRequest) {
        var statementProvider = new BaseStatementProvider(tenant);
        Map<String, Set<String>> udfs = fetchUdfRequest.getRequestData();

        tracingService.eventOnCurrentSpan("started");
        statementProvider
                .appendWithSingleArg(WORKTIME_AS_BOOKINGS_QUERY, personIds.toArray(UUID[]::new))
                .appendOptional("AND wt.endDateTime > ? ", bookingsFilter.getEarliest())
                .appendOptional("AND wt.startDateTime < ? ", bookingsFilter.getLatest())
                .append("UNION ALL ")

                .appendWithSingleArg(PERSON_RESERVATION_AS_BOOKINGS_QUERY, personIds.toArray(UUID[]::new))
                .appendOptional("AND per.endDate > ? ", bookingsFilter.getEarliest())
                .appendOptional("AND per.startDate < ? ", bookingsFilter.getLatest())
                .append("UNION ALL ")

                .appendWithMultipleArgs(
                        ACTIVITY_AS_BOOKINGS_QUERY,
                        bookingsFilter.isConsiderReleasedAsExclusive(),
                        bookingsFilter.isConsiderPlannedAsExclusive(),
                        personIds.toArray(UUID[]::new),
                        bookingsFilter.getEarliest(),
                        bookingsFilter.getLatest()
                );

        Map<UUID, List<Booking>> bookingsPerPerson = jdbcTemplate.query(statementProvider, bookingsResultSetExtractor);
        bookingsPerPerson = Objects.requireNonNullElseGet(bookingsPerPerson, HashMap::new);
        tracingService.eventOnCurrentSpan("booking-ids-and-dates-fetched");

        // ----- fetch activity UDFs -----
        List<UUID> bookingJobIds = collectJobIds(bookingsPerPerson);
        var udfBookingJobFilter = new FetchesFilter(udfs.get(FetchesType.JOB.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, List<Udf>> activityUdfMap = udfRepository.getUdfsByObjectIds(bookingJobIds, tenant, udfBookingJobFilter, ACTIVITY.getTableName());
        tracingService.eventOnCurrentSpan("activity-udf-fetched");

        // ----- fetch service call UDFs -----
        List<UUID> bookingServiceCallIds = collectJobServiceCallIds(bookingsPerPerson);
        var udfBookingServiceCallFilter = new FetchesFilter(udfs.get(FetchesType.JOB.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, List<Udf>> serviceCallUdfMap = udfRepository.getUdfsByObjectIds(bookingServiceCallIds, tenant, udfBookingServiceCallFilter, SERVICECALL.getTableName());
        tracingService.eventOnCurrentSpan("service-call-udf-fetched");

        // ----- fetch business partner UDFs -----
        List<UUID> bookingBusinessPartnerIds = collectJobBusinessPartnerIds(bookingsPerPerson);
        var udfBookingBusinessPartnerFilter = new FetchesFilter(udfs.get(FetchesType.BUSINESSPARTNER.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, Map<String, String>> businessPartnerMap = entityRepository.getEntityByEntityIds(bookingBusinessPartnerIds, tenant, udfBookingBusinessPartnerFilter, BUSINESSPARTNER.getTableName());
        tracingService.eventOnCurrentSpan("business-partner-udf-fetched");

        // ----- fetch booking equipment -----
        List<UUID> equipmentIds = collectJobEquipmentIds(bookingsPerPerson);
        var udfEquipmentFilter = new FetchesFilter(udfs.get(FetchesType.EQUIPMENT.name()), fetchUdfRequest.doNotReturn());
        Map<UUID, Map<String, String>> equipmentMap = entityRepository.getEntityByEntityIds(equipmentIds, tenant, udfEquipmentFilter, EQUIPMENT.getTableName());
        tracingService.eventOnCurrentSpan("booking-equipment-fetched");

        // ----- join all booking data -----
        for (List<Booking> bookings : bookingsPerPerson.values()) {
            for (Booking booking : bookings) {
                if (booking.hasJob()) {
                    booking.addJobUdfs(activityUdfMap.get(booking.getJob().getId()));
                    booking.addJobUdfs(serviceCallUdfMap.get(booking.getJob().getServiceCallId()));
                    booking.setJobEquipment(
                        booking.getJob().getEquipmentId() == null
                            ? null
                            : new Entity(booking.getJob().getEquipmentId(),
                            null,
                            equipmentMap.get(booking.getJob().getEquipmentId()))
                    );
                    booking.setJobBusinessPartner(
                        booking.getJob().getBusinessPartnerId() == null
                            ? null
                            : new Entity(booking.getJob().getBusinessPartnerId(),
                            null,
                            businessPartnerMap.get(booking.getJob().getBusinessPartnerId()))
                    );
                }
            }
        }

        return bookingsPerPerson;
    }

    private List<UUID> collectJobIds(Map<UUID, List<Booking>> map) {
        List<UUID> ids = new ArrayList<>();
        for (List<Booking> bookings : map.values()) {
            for (Booking booking : bookings) {
                if (booking.hasJob() && !ids.contains(booking.getJob().getId())) {
                    ids.add(booking.getJob().getId());
                }
            }
        }
        return ids;
    }

    private List<UUID> collectJobServiceCallIds(Map<UUID, List<Booking>> map) {
        List<UUID> ids = new ArrayList<>();
        for (List<Booking> bookings : map.values()) {
            for (Booking booking : bookings) {
                if (booking.relatesToServiceCall() && !ids.contains(booking.getJob().getServiceCallId())) {
                    ids.add(booking.getJob().getServiceCallId());
                }
            }
        }
        return ids;
    }

    private List<UUID> collectJobBusinessPartnerIds(Map<UUID, List<Booking>> map) {
        List<UUID> ids = new ArrayList<>();
        for (List<Booking> bookings : map.values()) {
            for (Booking booking : bookings) {
                if (booking.relatesToBusinessPartner() && !ids.contains(booking.getJob().getBusinessPartnerId())) {
                    ids.add(booking.getJob().getBusinessPartnerId());
                }
            }
        }
        return ids;
    }

    private List<UUID> collectJobEquipmentIds(Map<UUID, List<Booking>> map) {
        List<UUID> ids = new ArrayList<>();
        for (List<Booking> bookings : map.values()) {
            for (Booking booking : bookings) {
                if (booking.relatesToEquipment() && !ids.contains(booking.getJob().getEquipmentId())) {
                    ids.add(booking.getJob().getEquipmentId());
                }
            }
        }
        return ids;
    }
}
