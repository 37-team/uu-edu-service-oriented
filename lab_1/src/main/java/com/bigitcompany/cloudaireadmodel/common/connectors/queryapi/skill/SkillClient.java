package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.skill;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Tag;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.bigitcompany.cloudaireadmodel.aggregation.domain.services.SkillWorkTimePatternMapper.toWorkTimePattern;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.SKILL;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.TAG;
import static com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService.fromIsoDateStringToInstantEndOfTheDay;
import static com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService.fromIsoDateStringToInstantStartOfTheDay;
import static com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService.fromStringTimeToLocalTime;

@Component
public class SkillClient extends AbstractQueryApiClient {

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(SKILL, TAG);

    public static final String REQUEST_SKILLS_BASE = """
        SELECT\s
           skill.id, skill.person, skill.startDate, skill.endDate, skill.startTime, skill.endTime, skill.days,\s
           skill.tag, skill.lastChanged, tag.name\s
           FROM Skill skill, Tag tag\s
           WHERE skill.person IN ('%s')\s
           AND skill.tag = tag.id\s
        """;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public SkillClient(@Value("${service.query-api.host}") String queryApiHost,
                       @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<Skill>>> querySkillsByPersonIdsAsync(ReadModelRequestContext requestContext, List<UUID> personIds, FetchRequest fetchSkillRequest) {
        var skillPersonFilter = new FetchesFilter(fetchSkillRequest.getRequestData().get(FetchesType.RESOURCE.name()), fetchSkillRequest.doNotReturn());

        if (skillPersonFilter.doNotReturn()) {
            LOG.debug("No skills returned for person because of an empty fetch skill map.");
            return CompletableFuture.completedFuture(new HashMap<>());
        }

        var body = createQueryBody(personIds, skillPersonFilter.getNames());
        var pages = getAllPages(requestContext, REQUEST_DTOS, body, SkillQueryApiDto.class);
        var skills = unwrapPagesInSkillsByPersonId(pages);

        return CompletableFuture.completedFuture(skills);

    }

    // TODO FSMCPB-92261 Remove this method and move logic to async method
    public Map<UUID, List<Skill>> querySkillsByPersonIds(ReadModelRequestContext requestContext, List<UUID> personIds) {
        var body = createQueryBody(personIds, Collections.emptySet());
        var pages = getAllPages(requestContext, REQUEST_DTOS, body, SkillQueryApiDto.class);
        return unwrapPagesInSkillsByPersonId(pages);
    }

    private Map<String, String> createQueryBody(List<UUID> personIds, Set<String> skillsNames) {
        var selectQuery = REQUEST_SKILLS_BASE.formatted(String.join("','", UuidMapper.toFsmIds(personIds)));
        if (!skillsNames.isEmpty()) {
            selectQuery += String.format(" AND tag.name IN ('%s') ", String.join("','", skillsNames));
        }
        return Map.of("query", selectQuery);
    }

    private Map<UUID, List<Skill>> unwrapPagesInSkillsByPersonId(List<SkillQueryApiDto> pages) {
        return pages.stream()
            .flatMap(skill -> skill.getData().stream())
            .map(skillData -> {
                SkillQueryApiDto.Skill queryApiSkill = skillData.getSkill();
                Tag queryApiTag = skillData.getTag();

                final String skillId = queryApiSkill.getId();
                final String personId = queryApiSkill.getPerson();
                final String skillName = queryApiTag.name();
                final Instant startDate = fromIsoDateStringToInstantStartOfTheDay(queryApiSkill.getStartDate());
                final Instant endDate = fromIsoDateStringToInstantEndOfTheDay(queryApiSkill.getEndDate());
                final LocalTime startTime = fromStringTimeToLocalTime(queryApiSkill.getStartTime());
                final LocalTime endTime = fromStringTimeToLocalTime(queryApiSkill.getEndTime());
                final List<String> days = queryApiSkill.getDays();
                final WorkTimePattern validityPattern = toWorkTimePattern(startDate, endDate, startTime, endTime, days);

                return new Skill(
                    UuidMapper.toUUID(skillId),
                    skillName,
                    validityPattern,
                    UuidMapper.toUUID(personId),
                    null   // proficiency is set later after fetching proficiencies from another service
                );
            })
            .collect(Collectors.groupingBy(Skill::personId));
    }
}
