package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobIdsFilter;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class JobIdsFilterRepository {

    public static final String JOB_IDS_FILTER_QUERY = """
         SELECT DISTINCT a.id, t.name\s
         FROM Activity a\s
         LEFT JOIN Requirement r ON      a.id = r.activity\s
         LEFT JOIN Tag t         ON      t.id = r.tag\s
        """;

    private static final String ACTIVITY_FILTER_QUERY = " WHERE a.id = ANY (?)\n";

    private static final String REQUIREMENT_FILTER_QUERY = "t.name = ?\n";

    private static final String ORDER_BY_ID_QUERY = "ORDER BY a.id;";

    private final JdbcTemplate jdbcTemplate;

    private final ActiveTenantProvider activeTenantProvider;

    private final ResultSetExtractor<Map<String, List<String>>> jobIdByRequirementExtractor = rs -> {
        Map<String, List<String>> result = new HashMap<>();
        while (rs.next()) {
            String jobId = UuidMapper.toFsmId(UUID.fromString(rs.getString("id")));
            String requirementName = rs.getString("name");
            result.computeIfAbsent(requirementName, k -> new ArrayList<>()).add(jobId);
        }
        return result;
    };

    public JobIdsFilterRepository(ActiveTenantProvider activeTenantProvider,
                                  JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.activeTenantProvider = activeTenantProvider;
    }


    public Map<String, List<String>> filterAndPartitionJobIdsByRequirement(JobIdsFilter filter) {
        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();

        var statementProvider = new BaseStatementProvider(tenant, JOB_IDS_FILTER_QUERY);
        statementProvider.appendWithSingleArg(ACTIVITY_FILTER_QUERY, filter.ids().toArray(UUID[]::new));
        statementProvider.append("AND (");
        for (Iterator<String> it = filter.requirements().iterator(); it.hasNext(); ) {
            String requirement = it.next();
            statementProvider.appendWithSingleArg(REQUIREMENT_FILTER_QUERY, requirement);
            if (it.hasNext()) {
                statementProvider.append("OR ");
            }
        }
        statementProvider.append(")");
        statementProvider.append(ORDER_BY_ID_QUERY);

        return jdbcTemplate.query(statementProvider, jobIdByRequirementExtractor);
    }
}
