package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceIdsFilter;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SKILL;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.TAG;

@Component
public class ResourceIdsClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(PERSON, SKILL, TAG);

    private static final String PERSON_FILTER_QUERY = "WHERE person.id IN ('%s') ";

    private static final String SKILL_FILTER_QUERY = "t.name IN ('%s') ";

    private static final String ORDER_BY_CLAUSE = "ORDER BY person.id ASC ";

    public ResourceIdsClient(@Value("${service.query-api.host}") String queryApiHost,
                             @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    public List<UUID> filterResourcesBySkills(ReadModelRequestContext requestContext, ResourceIdsFilter filter) {
        var body = createQueryBody(filter);
        var page = fetchFirstPageWithLimit(requestContext, REQUEST_DTOS, body, ResourceIdQueryApiDto.class, filter.limit());
        return unwrapPagesInResourceIds(page);
    }

    private Map<String, String> createQueryBody(ResourceIdsFilter filter) {
        var sqlSelect = new StringBuilder("""
                SELECT DISTINCT person.id\s
                FROM Person person\s
                LEFT JOIN Skill s on person.id = s.person\s
                LEFT JOIN Tag t on t.id = s.tag\s
            """);
        var wasWhereAlreadyAppended = false;
        if (!filter.ids().isEmpty()) {
            sqlSelect.append(String.format(PERSON_FILTER_QUERY, String.join("','", filter.ids().stream().map(UuidMapper::toFsmId).toList())));
            wasWhereAlreadyAppended = true;
        }
        if (filter.skills() != null && !filter.skills().isEmpty()) {
            appendWhereOrAnd(wasWhereAlreadyAppended, sqlSelect);
            var skillsInClause = String.join("','", filter.skills());
            sqlSelect.append(String.format(SKILL_FILTER_QUERY, skillsInClause));
            wasWhereAlreadyAppended = true;
        }
        if (filter.includeCrowdPersons() && !filter.includeInternalPersons()) {
            appendWhereOrAnd(wasWhereAlreadyAppended, sqlSelect);
            sqlSelect.append("person.crowdType IS NOT NULL AND person.crowdType != 'NON_CROWD' ");
        } else if (filter.includeInternalPersons() && !filter.includeCrowdPersons()) {
            appendWhereOrAnd(wasWhereAlreadyAppended, sqlSelect);
            sqlSelect.append("(person.crowdType IS NULL OR person.crowdType = 'NON_CROWD') ");
        }
        sqlSelect.append(ORDER_BY_CLAUSE);
        return Map.of("query", sqlSelect.toString());
    }

    private List<UUID> unwrapPagesInResourceIds(ResourceIdQueryApiDto page) {
        return page.getData().stream()
            .map(dataDto -> UuidMapper.toUUID(dataDto.getPerson().id()))
            .toList();
    }

    private void appendWhereOrAnd(boolean wasWhereAlreadyAppended, StringBuilder builder) {
        if (wasWhereAlreadyAppended) {
            builder.append("AND ");
        } else {
            builder.append("WHERE ");
        }
    }
}
