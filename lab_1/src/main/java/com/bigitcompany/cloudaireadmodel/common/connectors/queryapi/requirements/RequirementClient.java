package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.requirements;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Proficiency;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Requirement;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ACTIVITY;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.REQUIREMENT;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.TAG;

@Component
public class RequirementClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(REQUIREMENT, ACTIVITY, TAG);

    public RequirementClient(@Value("${service.query-api.host}") String queryApiHost,
                             @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<Requirement>>> queryRequirementsByActivityIdsAsync(ReadModelRequestContext requestContext, List<String> activityIds) {
        var body = createQueryBody(activityIds);
        var pages = super.getAllPages(requestContext, REQUEST_DTOS, body, RequirementQueryApiDto.class);
        var requirementsByActivityId = unwrapPagesToRequirementsByActivityId(pages);

        if (LOG.isDebugEnabled()) {
            requirementsByActivityId.forEach((activityId, requirements) -> {
                var tags = requirements.stream().map(Requirement::tag).distinct().collect(Collectors.joining(","));
                LOG.debug(String.format("activity=%s,requirements=%d,tags=%s", activityId, requirements.size(), tags));
            });
        }

        return CompletableFuture.completedFuture(requirementsByActivityId);
    }

    private Map<UUID, List<Requirement>> unwrapPagesToRequirementsByActivityId(List<RequirementQueryApiDto> pages) {
        return pages.stream()
            .flatMap(requirementDto -> requirementDto.getData().stream())
            .map(requirementData -> {
                var queryApiActivity = requirementData.getActivity();
                var queryApiRequirement = requirementData.getRequirement();
                var queryApiTag = requirementData.getTag();

                var tagName = queryApiTag.name();
                var activityId = queryApiActivity.getId();
                var mandatory = queryApiRequirement.isMandatory();

                return new Requirement(
                    tagName,
                    UuidMapper.toUUID(activityId),
                    mandatory,
                    Proficiency.REQUIRED_PROFICIENCY
                );
            })
            .collect(Collectors.groupingBy(Requirement::activity));
    }

    private Map<String, String> createQueryBody(List<String> activityIds) {
        String sqlSelect = """
            SELECT\s
                activity.id,\s
                requirement.mandatory,\s
                requirement.tag,\s
                tag.name\s
             FROM Requirement requirement,\s
                 Tag tag,\s
                 Activity activity\s
             WHERE\s
                 requirement.object.objectId IN ('%s')\s
             AND\s
                 requirement.tag = tag.id\s
             AND\s
                 requirement.object.objectId = activity.id\s
             ORDER BY\s
                 requirement.id ASC
            """.formatted(String.join("','", activityIds));
        LOG.debug(sqlSelect);
        return Map.of("query", sqlSelect);
    }
}
