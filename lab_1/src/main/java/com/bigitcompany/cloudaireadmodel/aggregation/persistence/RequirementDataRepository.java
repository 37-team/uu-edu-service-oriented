package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Requirement;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.RequirementSetExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class RequirementDataRepository {

    private static final String FETCH_REQUIREMENT_TAG =
        "SELECT "
            + " requirement.id as requirement_id, requirement.mandatory as requirement_mandatory, requirement.activity as requirement_activity, requirement.tag as requirement_tag, "
            + " tag.id as tag_id, tag.name as tag_name "
            + " FROM requirement "
            + " INNER JOIN tag ON tag.id=requirement.tag "
            + " WHERE requirement.activity = ANY (?)";


    private final JdbcTemplate jdbcTemplate;

    public RequirementDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<UUID, List<Requirement>> getByActivityIds(List<UUID> ids, String tenant) {
        PreparedStatementCreator statementCreator = new TenantSqlPreparedStatementProvider(FETCH_REQUIREMENT_TAG, tenant).setArg(ids.toArray(UUID[]::new));
        return jdbcTemplate.query(statementCreator, new RequirementSetExtractor());
    }

}
