package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingJob;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import jakarta.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.RowMapperHelper.mapPgObjectTo;

public class BookingsResultSetExtractor implements ResultSetExtractor<Map<UUID, List<Booking>>> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String JSON_ROW_IDENTIFIER = "json_row";

    public static final String ACTIVITY_BOOKING_TYPE = "activity";

    public static final String BOOKING_LOCATION= "location";

    @Override
    public Map<UUID, List<Booking>> extractData(ResultSet rs) throws SQLException {
        Map<UUID, List<Booking>> bookingsByPerson = new HashMap<>();
        while (rs.next()) {
            try {
                var personId = UuidMapper.toUUID(rs.getString("person"));
                var bookingType = rs.getString("booking_type");
                var start = rs.getObject("start", Date.class).toInstant();
                var end = rs.getObject("end", Date.class).toInstant();
                var exclusive = rs.getBoolean("exclusive");
                var location = extractAddress(rs);

                var bookingBuilder = new Booking.Builder()
                    .start(start)
                    .end(end)
                    .exclusive(exclusive)
                    .location(location);

                if (ACTIVITY_BOOKING_TYPE.equals(bookingType)) {
                    // This will also do booking.location = job.location
                    bookingBuilder.job(mapActivityToBookingJob(rs));
                }

                bookingsByPerson.computeIfAbsent(personId, k -> new ArrayList<>()).add(bookingBuilder.build());
            } catch (IllegalStateException e) {
                LOG.warn("Invalid data while processing booking, booking discarded.", e);
            }
        }
        return bookingsByPerson;
    }

    @NotNull
    private BookingJob mapActivityToBookingJob(ResultSet rs) throws SQLException {
        Map<String, Object> queryMap;
        try {
            queryMap = objectMapper.readValue(rs.getString(JSON_ROW_IDENTIFIER), new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new SQLException("JSON parsing error while processing booking activity.", e);
        }
        UUID businessPartnerId = null;
        if (queryMap.get("activity_business_partner_id") != null) {
            businessPartnerId = UuidMapper.toUUID((String) queryMap.get("activity_business_partner_id"));
        } else if (queryMap.get("service_call_business_partner_id") != null){
            businessPartnerId = UuidMapper.toUUID((String) queryMap.get("service_call_business_partner_id"));
        }

        return new BookingJob(
            UuidMapper.toUUID((String) queryMap.get("activity_id")),
            UuidMapper.toUUID((String) queryMap.get("service_call_id")),
            DateTimeService.toZonedDateTime((String) queryMap.get("activity_lastchanged")).toInstant(),
            queryMap.get(BOOKING_LOCATION) != null ? objectMapper.convertValue(queryMap.get(BOOKING_LOCATION), Location.class) : null,
            UuidMapper.toUUID((String) queryMap.get("e_id")),
            businessPartnerId
        );
    }

    private Location extractAddress(ResultSet rs) {
        Location location;
        try {
            location = mapPgObjectTo(rs.getObject(BOOKING_LOCATION), Location.class);
        } catch (SQLException | JsonProcessingException e) {
            var message = "Error processing location for resource.";
            LOG.error(message + e.getMessage(), e);
            throw new DomainException(message);
        }
        return location;
    }
}