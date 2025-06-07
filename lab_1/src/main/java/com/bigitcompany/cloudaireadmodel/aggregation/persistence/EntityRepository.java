package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.EntityResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class EntityRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String ENTITY_PLACEHOLDER = ":entity";

    private static final String QUERY_TEMPLATE = """
        SELECT
            entity.id AS id,
            entity.externalId AS externalId,
            meta.name AS udfName,
            entity.value AS udfValue
        FROM (
            SELECT
                e.id,
                e.externalId,
                jsonb_array_elements(e.udfValues)->>'meta' as metaId,
                jsonb_array_elements(e.udfValues)->>'value' as value
            FROM :entity e
            ) AS entity
        LEFT JOIN udfMeta meta ON entity.metaId::uuid = meta.id
        WHERE entity.id = ?::uuid
        """;

    private final JdbcTemplate jdbcTemplate;

    private final ResultSetExtractor<Entity> entityResultSetExtractor;


    EntityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        entityResultSetExtractor = new EntityResultSetExtractor();

    }

    public Entity getByObjectId(UUID objectId, String tenantKey, String objectName) {
        String query = QUERY_TEMPLATE.replace(ENTITY_PLACEHOLDER, objectName);
        return this.query(tenantKey, query, objectId);
    }

    private Entity query(String tenantKey, String query, UUID id) {
        PreparedStatementCreator statementCreator = new TenantSqlPreparedStatementProvider(query, tenantKey)
            .addSingleArg(id);
        return jdbcTemplate.query(statementCreator, entityResultSetExtractor);
    }

}