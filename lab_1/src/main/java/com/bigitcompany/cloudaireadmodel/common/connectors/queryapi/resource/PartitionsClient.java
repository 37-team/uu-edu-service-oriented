package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourcePartitionRequestDto;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SKILL;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.TAG;

@Component
public class PartitionsClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(PERSON, SKILL, TAG);

    private static final String PERSON_FILTER_QUERY = "WHERE person.id IN ('%s') ";

    private static final String SKILL_FILTER_QUERY = " tag.name IN ('%s') ";

    private static final String ORDER_BY_CLAUSE = "ORDER BY person.id ASC ";

    public PartitionsClient(@Value("${service.query-api.host}") String queryApiHost,
                            @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    public Map<String, List<String>> fetchPersonsAndSkills(ReadModelRequestContext requestContext, ResourcePartitionRequestDto filter) {
        var body = createQueryBody(filter);
        var page = fetchFirstPage(requestContext, REQUEST_DTOS, body, PartitionsQueryApiDto.class);
        return unwrapPagesInSinglePartitions(page);
    }

    private Map<String, String> createQueryBody(ResourcePartitionRequestDto filter) {
        var sqlSelect = new StringBuilder("""
            SELECT\s
                person.id,\s
                tag.name\s
            FROM\s
                Person person\s
            LEFT JOIN Skill skill on person.id = skill.person\s
            LEFT JOIN Tag tag on tag.id = skill.tag \s
            """);
        sqlSelect.append(
                String.format(
                        PERSON_FILTER_QUERY,
                        String.join("','", filter.ids().stream().map(UuidMapper::toFsmId).toList())
                )
        ).append(" AND ");
        var skillsInClause = String.join("','", filter.skillNames());
        sqlSelect.append(String.format(SKILL_FILTER_QUERY, skillsInClause, skillsInClause));
        sqlSelect.append(ORDER_BY_CLAUSE);
        return Map.of("query", sqlSelect.toString());
    }

    private Map<String, List<String>> unwrapPagesInSinglePartitions(PartitionsQueryApiDto page) {
        Map<String, List<String>> result = new HashMap<>();
        page.getData().forEach(dataDto -> {
                var personId = UuidMapper.toFsmId(dataDto.getPerson().id());
                var skillName = dataDto.getTag().name();
                result.computeIfAbsent(skillName, k -> new ArrayList<>()).add(personId);
            });
        return result;
    }
}
