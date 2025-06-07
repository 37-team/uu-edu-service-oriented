package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobIdsFilter;
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
import java.util.stream.Collectors;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ACTIVITY;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.REQUIREMENT;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.TAG;

@Component
public class ActivityAndRequirementClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(ACTIVITY, TAG, REQUIREMENT);

    protected ActivityAndRequirementClient(@Value("${service.query-api.host}") String queryApiHost,
                                           @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    public Map<String, List<String>> filterAndPartitionJobsByIdsAndRequirements(ReadModelRequestContext requestContext, JobIdsFilter filter) {
        var body = createQueryBody(filter);
        var page = fetchFirstPage(requestContext, REQUEST_DTOS, body, ActivityAndTagDto.class);
        return extractPartitions(page);
    }

    private Map<String, String> createQueryBody(JobIdsFilter filter) {
        var tagFilters = filter.requirements().stream()
            .map(tagName -> tagName.replace("'", "\\'"))
            .map(escapedTagName -> String.format("tag.name LIKE '%s'", escapedTagName)).toList();

        var sqlSelect = """
            SELECT DISTINCT activity.id, tag.name\s
            FROM Activity activity, Requirement requirement, Tag tag\s
            WHERE activity.id IN ('%s')\s
            AND activity.id = requirement.object.objectId\s
            AND requirement.tag = tag.id AND (%s)
            ORDER BY tag.name\s
            """.formatted(
            String.join("','", filter.ids().stream().map(UuidMapper::toFsmId).toList()),
            String.join(" OR ", tagFilters)
        );
        return Map.of("query", sqlSelect);
    }

    private Map<String, List<String>> extractPartitions(ActivityAndTagDto page) {
        return page.getData().stream()
            .collect(Collectors.groupingBy(
                activityAndTagData -> activityAndTagData.getTag().name(),
                Collectors.mapping(activityAndTagData -> activityAndTagData.getActivity().id(), Collectors.toList())
            ));
    }
}
