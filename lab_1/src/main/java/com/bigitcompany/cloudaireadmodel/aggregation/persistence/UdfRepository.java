package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.UdfRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class UdfRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ENTITY_PLACEHOLDER = ":entity";

    private static final int MAX_UDF_COUNT_PER_ENTITY = 100;

    private static final String QUERY_TEMPLATE = "SELECT " +
        "    e.id AS entity," +
        "    u.name AS udf_name," +
        "    x.udfvalues->>'value' AS udf_value," +
        "    uuid(x.udfvalues->>'meta') AS meta_uuid" +
        " FROM " + ENTITY_PLACEHOLDER + " e, LATERAL (select id, jsonb_array_elements(udfvalues) as udfvalues from :entity WHERE id = e.id) as x" +
        " LEFT JOIN udfmeta u ON  u.id = uuid(x.udfvalues->>'meta')" +
        " WHERE u.name is not null " +
        " AND e.id = ANY (?) " +
        " LIMIT ?";

    private final JdbcTemplate jdbcTemplate;

    UdfRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<UUID, List<Udf>> getUdfsByObjectIds(List<UUID> objectIds, String tenantKey, FetchesFilter udfFilter, String objectName) {
        if (objectIds.isEmpty()) {
            LOG.debug(String.format("No %s IDs provided for fetching object UDFs", objectName));
            return new HashMap<>();
        }
        if (udfFilter.doNotReturn()) {
            LOG.debug("No UDFs returned for {} because of an empty fetch UDF map.", objectName);
            return new HashMap<>();
        }
        String query = QUERY_TEMPLATE.replace(ENTITY_PLACEHOLDER, objectName);
        return this.query(tenantKey, query, objectIds, udfFilter);
    }

    private Map<UUID, List<Udf>> query(String tenantKey, String query, List<UUID> uuids, FetchesFilter udfFilter) {
        PreparedStatementCreator statementCreator = new TenantSqlPreparedStatementProvider(query, tenantKey)
            .addSingleArg(uuids.toArray(UUID[]::new))
            .addSingleArg(uuids.size() * MAX_UDF_COUNT_PER_ENTITY);
        List<Udf> results = jdbcTemplate.query(statementCreator, new RowMapperResultSetExtractor<>(new UdfRowMapper()));
        return this.mapByEntity(results, udfFilter);
    }

    private Map<UUID, List<Udf>> mapByEntity(List<Udf> list, FetchesFilter udfFilter) {
        Map<UUID, List<Udf>> map = new HashMap<>();
            for (Udf udf : Optional.ofNullable(list).orElse(new ArrayList<>())) {
                if (udfFilter.shouldInclude(udf.getName())) {
                    if (!map.containsKey(udf.getEntityId())) {
                        map.put(udf.getEntityId(), new ArrayList<>());
                    }
                    map.get(udf.getEntityId()).add(udf);
                }
            }
        return map;
        }
}
