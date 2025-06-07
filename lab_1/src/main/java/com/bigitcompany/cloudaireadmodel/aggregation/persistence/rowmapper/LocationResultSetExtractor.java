package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.RowMapperHelper.mapPgObjectTo;

public class LocationResultSetExtractor implements ResultSetExtractor<Map<UUID, Location>> {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Map<UUID, Location> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, Location> locationsByPerson = new HashMap<>();
        while (rs.next()) {
            var personId = UuidMapper.toUUID(rs.getString("person"));
            locationsByPerson.computeIfAbsent(personId, k -> extractLocation(rs));
        }

        return locationsByPerson;
    }

    // CAUTION: the order in which the objects are added matters and defines
    // which Location will be picked as the resources homeLocation
    private static final String[] LOCATION_COLUMN_NAMES = {
        // DEFAULT: https://jira.coresystems.net/browse/CPB-71637
        "empl_home_default_location", "empl_work_default_location",
        "erp_home_default_location", "erp_work_default_location",

        // NOT DEFAULT
        "empl_home_location", "empl_work_location",
        "erp_home_location", "erp_work_location"
    };

    private Location extractLocation(ResultSet rs) {
        Location homeLocation;
        try {
            var columnNameIndex = 0;
            do {
                homeLocation = mapPgObjectTo(rs.getObject(LOCATION_COLUMN_NAMES[columnNameIndex++]), Location.class);
            } while (homeLocation == null && columnNameIndex < LOCATION_COLUMN_NAMES.length);
        } catch (SQLException | JsonProcessingException e) {
            var message = "Error processing location for resource.";
            LOG.error(message + e.getMessage(), e);
            throw new DomainException(message);
        }
        return homeLocation;
    }
}
