package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ExecutionStage;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.model.ActivityData;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.RowMapperHelper.mapPgObjectTo;

public class ActivityDataRowMapper implements RowMapper<ActivityData> {

    private static final Logger logger = LoggerFactory.getLogger(ActivityDataRowMapper.class);

    private static final String ACTIVITY_START_PROPERTY = "activity_startdatetime";

    private static final String ACTIVITY_END_PROPERTY = "activity_enddatetime";

    private static final String ACTIVITY_RESPONSIBLES_PROPERTY = "activity_responsibles";


    /**
     * Returns a partial Job object.
     * Certain fields are omitted (have default values) as the result set does not contain this information.
     * Additional DB / API calls are needed to append this data later.
     */
    @Override
    public ActivityData mapRow(ResultSet rs, int rowNum) throws SQLException {
        var location = mapToLocation(rs);

        return new ActivityData(
                UuidMapper.toUUID(rs.getString("activity_id")),
                rs.getString("activity_external_id"),
                UuidMapper.toUUID(rs.getString("activity_servicecall"), false),
                rs.getString("servicecall_external_id"),
                UuidMapper.toUUID(rs.getString("activity_equipment")),
                rs.getString("equipment_external_id"),
                UuidMapper.toUUID(rs.getString("activity_businesspartner")),
                rs.getString("businesspartner_external_id"),
                DateTimeService.toInstant(rs.getTimestamp("activity_earlieststartdatetime")),
                DateTimeService.toInstant(rs.getTimestamp("activity_duedatetime")),
                location,
                (Integer) rs.getObject("activity_durationinminutes"),
                ExecutionStage.valueOf(rs.getString("activity_executionstage")),
                rs.getString("activity_syncstatus"),
                uuidList(rs.getArray(ACTIVITY_RESPONSIBLES_PROPERTY)),
                DateTimeService.toInstant(rs.getTimestamp(ACTIVITY_START_PROPERTY)),
                DateTimeService.toInstant(rs.getTimestamp(ACTIVITY_END_PROPERTY)),
                DateTimeService.toInstant(rs.getTimestamp("activity_lastchanged")),
                (Integer) rs.getObject("activity_travel_time_to_in_minutes"),
                (Integer) rs.getObject("activity_travel_time_from_in_minutes"),
                rs.getString("servicecall_priority"),
                (Integer) rs.getObject("activity_planneddurationinminutes")
        );
    }

    private Location mapToLocation(ResultSet rs) throws SQLException {
        var addressId = UuidMapper.toUUID(rs.getString("address_id"));
        if (addressId == null) {
            return null;
        }

        try {
            return mapPgObjectTo(rs.getObject("address_location"), Location.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse address location: ", e);
            throw new RuntimeException(e);
        }
    }

    private List<UUID> uuidList(Array value) throws SQLException {
        if (value == null) {
            return new ArrayList<>();
        }

        Object[] array = (Object[]) value.getArray();

        if (array.length == 0) {
            return new ArrayList<>();
        } else if (array[0] instanceof UUID) {
            return List.of((UUID[]) array);
        } else if (array[0] instanceof String) {
            return Stream.of(array)
                .map(uuidString -> UuidMapper.toUUID((String) uuidString))
                .toList();
        } else {
            throw new IllegalArgumentException("The array must be the type of String or type of UUID");
        }
    }
}
