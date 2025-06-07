package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ResourcePartitionRepository {

    private static final int LIMIT_MAX = 1000;

    private static final String RESOURCE_PARTITION_FILTER_QUERY = """                
                SELECT\s
                    p.id, t.name\s
                FROM Person p\s
                LEFT JOIN Skill s on p.id = s.person\s
                LEFT JOIN Tag t on t.id = s.tag\s
            """;

    private static final String IDS_FILTER_QUERY = "WHERE p.id = ANY (?)\n";

    private static final String SKILL_FILTER_QUERY = " t.name = ANY (?)\n";

    private static final String ORDER_BY_ID_QUERY = "ORDER BY p.id\n";

    private static final String LIMIT_QUERY = "LIMIT ?;";

    private final JdbcTemplate jdbcTemplate;

    private final ActiveTenantProvider activeTenantProvider;

    private final ResultSetExtractor<Map<String, List<String>>> resourcePartitionsExtractor = rs -> {
        Map<String, List<String>> result = new HashMap<>();
        while (rs.next()) {
            var personId = UuidMapper.toFsmId(rs.getObject("id", UUID.class));
            var skillName = rs.getObject("name", String.class);
            result.computeIfAbsent(skillName, k -> new ArrayList<>()).add(personId);
        }
        return result;
    };

    public ResourcePartitionRepository(JdbcTemplate jdbcTemplate, ActiveTenantProvider activeTenantProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.activeTenantProvider = activeTenantProvider;
    }

    public Map<String, List<String>> getPartitions(List<UUID> ids, List<String> skillNames) {
        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();
        var statementProvider = new BaseStatementProvider(tenant, RESOURCE_PARTITION_FILTER_QUERY);
        statementProvider.appendWithSingleArg(IDS_FILTER_QUERY, ids.toArray(UUID[]::new));
        statementProvider.append(" AND ");
        statementProvider.appendWithMultipleArgs(SKILL_FILTER_QUERY, (Object) skillNames.toArray(String[]::new));
        statementProvider.append(ORDER_BY_ID_QUERY);
        statementProvider.appendWithSingleArg(LIMIT_QUERY, LIMIT_MAX);
        return jdbcTemplate.query(statementProvider, resourcePartitionsExtractor);
    }
}
