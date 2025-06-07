package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceIdsFilter;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class ResourceIdsFilterRepository {

    private static final String RESOURCE_ID_FILTER_QUERY = """
            SELECT DISTINCT p.id\s
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

    public ResourceIdsFilterRepository(JdbcTemplate jdbcTemplate, ActiveTenantProvider activeTenantProvider) {
        this.jdbcTemplate = jdbcTemplate;
        this.activeTenantProvider = activeTenantProvider;
    }

    public List<UUID> filterAndLimitResourceIds(ResourceIdsFilter filter) {
        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();
        var statementProvider = new BaseStatementProvider(tenant, RESOURCE_ID_FILTER_QUERY);
        var wasWhereAlreadyAppended = false;

        if (!filter.ids().isEmpty()) {
            statementProvider.appendWithSingleArg(IDS_FILTER_QUERY, filter.ids().toArray(UUID[]::new));
            wasWhereAlreadyAppended = true;
        }

        if (filter.skills() != null && !filter.skills().isEmpty()) {
            appendWhereOrAnd(wasWhereAlreadyAppended, statementProvider);
            statementProvider.appendWithMultipleArgs(SKILL_FILTER_QUERY, (Object) filter.skills().toArray(String[]::new));
            wasWhereAlreadyAppended = true;
        }
        considerPersonsFilter(wasWhereAlreadyAppended, statementProvider, filter.includeCrowdPersons(), filter.includeInternalPersons());

        statementProvider.append(ORDER_BY_ID_QUERY);
        statementProvider.appendWithSingleArg(LIMIT_QUERY, filter.limit());

        return jdbcTemplate.query(statementProvider, (rs, rowNum) -> rs.getObject("id", UUID.class));
    }

    private void considerPersonsFilter(boolean wasWhereAlreadyAppended, BaseStatementProvider statementProvider, boolean includeCrowdPersons, boolean includeInternalPersons) {
        if (includeCrowdPersons && !includeInternalPersons) {
            appendWhereOrAnd(wasWhereAlreadyAppended, statementProvider);
            statementProvider.append(" p.crowdtype IS NOT NULL AND p.crowdtype != 'NON_CROWD'\n");
        } else if (includeInternalPersons && !includeCrowdPersons) {
            appendWhereOrAnd(wasWhereAlreadyAppended, statementProvider);
            statementProvider.append(" (p.crowdtype IS NULL OR p.crowdtype = 'NON_CROWD')\n");
        }
    }

    private void appendWhereOrAnd(boolean wasWhereAlreadyAppended, BaseStatementProvider statementProvider) {
        if (wasWhereAlreadyAppended) {
            statementProvider.append("AND ");
        } else {
            statementProvider.append("WHERE ");
        }
    }
}
