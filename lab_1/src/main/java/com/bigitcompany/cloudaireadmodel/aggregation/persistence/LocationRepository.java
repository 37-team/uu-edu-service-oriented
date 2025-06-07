package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.LocationResultSetExtractor;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class LocationRepository {

    private static final String LOCATION_QUERY = "" +
        "SELECT erpUser.id as person, \n" +

        // Default: https://jira.coresystems.net/browse/CPB-71637
        "    employeeAddressHomeDefault.location as empl_home_default_location, \n" +
        "    employeeAddressWorkDefault.location as empl_work_default_location, \n" +
        "    erpUserAddressHomeDefault.location as erp_home_default_location, \n" +
        "    erpUserAddressWorkDefault.location as erp_work_default_location, \n" +

        // Not default
        "    employeeAddressHome.location as empl_home_location, \n" +
        "    employeeAddressWork.location as empl_work_location, \n" +
        "    erpUserAddressHome.location as erp_home_location, \n" +
        "    erpUserAddressWork.location as erp_work_location \n" +

        // ERP user
        "FROM Person as erpUser \n" +

        // erp user - home - default
        "LEFT JOIN Address erpUserAddressHomeDefault \n" +
        "ON erpUserAddressHomeDefault.objectId = erpUser.id \n" +
        "AND erpUserAddressHomeDefault.type = 'HOME' \n" +
        "AND erpUserAddressHomeDefault.defaultAddress = true \n" +

        // erp user - work - default
        "LEFT JOIN Address erpUserAddressWorkDefault \n" +
        "ON erpUserAddressWorkDefault.objectId = erpUser.id \n" +
        "AND erpUserAddressWorkDefault.type = 'WORK' \n" +
        "AND erpUserAddressWorkDefault.defaultAddress = true \n" +

        // erp user - home - not default
        "LEFT JOIN Address erpUserAddressHome \n" +
        "ON erpUserAddressHome.objectId = erpUser.id \n" +
        "AND erpUserAddressHome.type = 'HOME' \n" +
        "AND erpUserAddressHome.defaultAddress = false \n" +

        // erp user - work - not default
        "LEFT JOIN Address erpUserAddressWork \n" +
        "ON erpUserAddressWork.objectId = erpUser.id \n" +
        "AND erpUserAddressWork.type = 'WORK' \n" +
        "AND erpUserAddressWork.defaultAddress = false \n" +

        // Employee
        "LEFT JOIN Person employee \n" +
        "ON employee.refId = erpUser.refId \n" +
        "AND employee.type = 'EMPLOYEE' \n" +

        // employee - home - default
        "LEFT JOIN Address employeeAddressHomeDefault \n" +
        "ON employeeAddressHomeDefault.objectId = employee.id \n" +
        "AND employeeAddressHomeDefault.type = 'HOME' \n" +
        "AND employeeAddressHomeDefault.defaultAddress = true \n" +

        // employee - work - default
        "LEFT JOIN Address employeeAddressWorkDefault \n" +
        "ON employeeAddressWorkDefault.objectId = employee.id \n" +
        "AND employeeAddressWorkDefault.type = 'WORK' \n" +
        "AND employeeAddressWorkDefault.defaultAddress = true \n" +

        // employee - home - not default
        "LEFT JOIN Address employeeAddressHome \n" +
        "ON employeeAddressHome.objectId = employee.id \n" +
        "AND employeeAddressHome.type = 'HOME' \n" +
        "AND employeeAddressHome.defaultAddress = false \n" +

        // employee - work - not default
        "LEFT JOIN Address employeeAddressWork \n" +
        "ON employeeAddressWork.objectId = employee.id \n" +
        "AND employeeAddressWork.type = 'WORK' \n" +
        "AND employeeAddressWork.defaultAddress = false \n" +

        "WHERE erpUser.type = 'ERPUSER' \n" +
        "AND erpUser.id = ANY (?) \n" +
        "ORDER BY erpUser.id";

    private final JdbcTemplate jdbcTemplate;

    private final ResultSetExtractor<Map<UUID, Location>> locationResultSetExtractor;

    public LocationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        locationResultSetExtractor = new LocationResultSetExtractor();
    }

    public Map<UUID, Location> fetchLocationsForResources(List<UUID> personIds, String tenant) {
        PreparedStatementCreator fetchLocationStatement = new TenantSqlPreparedStatementProvider(LOCATION_QUERY, tenant).setArg(personIds.toArray(UUID[]::new));
        return jdbcTemplate.query(fetchLocationStatement, locationResultSetExtractor);
    }
}
