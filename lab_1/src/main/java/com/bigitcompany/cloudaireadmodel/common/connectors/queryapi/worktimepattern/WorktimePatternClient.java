package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.worktimepattern;

import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.DayPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.TimeRange;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WeekPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSONWORKTIMEPATTERN;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.WORKTIMEPATTERN;

@Component
public class WorktimePatternClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(WORKTIMEPATTERN, PERSON, PERSONWORKTIMEPATTERN);

    public WorktimePatternClient(@Value("${service.query-api.host}") String queryApiHost,
                                 @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<WorkTimePattern>>> queryWorkTimePatternsByPersonIds(ReadModelRequestContext requestContext, List<UUID> personIds) {
        var body = createQueryBody(personIds);
        var pages = getAllPages(requestContext, REQUEST_DTOS, body, WorktimePatternApiDto.class);
        return CompletableFuture.completedFuture(unwrapPagesByPersonId(pages));
    }

    private Map<String, String> createQueryBody(List<UUID> personIds) {
        String sqlSelect = "SELECT\n" +
            "                           person.id,\n" +
            "                           personWorkTimePattern.id,\n" +
            "                           personWorkTimePattern.startDate,\n" +
            "                           personWorkTimePattern.endDate,\n" +
            "                           workTimePattern.id,\n" +
            "                           workTimePattern.weeks\n" +
            "                         FROM\n" +
            "                           Person person,\n" +
            "                           PersonWorkTimePattern personWorkTimePattern,\n" +
            "                           WorkTimePattern workTimePattern\n" +
            "                         WHERE\n" +
            "                           workTimePattern.id = personWorkTimePattern.workTimePattern AND\n" +
            "                           personWorkTimePattern.person = person.id AND\n" +
            "                           person.id IN ('" + String.join("','", personIds.stream().map(UuidMapper::toFsmId).toList()) + "')\n" +
            "                         ORDER BY person.id ASC, personWorkTimePattern.id ASC\n" +
            "    ";
        return Map.of("query", sqlSelect);
    }

    private Map<UUID, List<WorkTimePattern>> unwrapPagesByPersonId(List<WorktimePatternApiDto> pages) {
        Map<UUID, List<WorkTimePattern>> wtps = new HashMap<>();
        pages.stream()
            .flatMap(d -> d.getData().stream())
            .forEach(dataDto -> {
                var personWorktimePattern = dataDto.personWorkTimePattern();
                var worktimePattern = dataDto.workTimePattern();
                var personId = UuidMapper.toUUID(dataDto.person().id());

                if (personWorktimePattern != null && worktimePattern != null) {
                    List<WeekPattern> weeks = worktimePattern.weeks().stream()
                        .map(week -> {
                            var days = week.days().stream().map(dayPattern -> {
                                List<TimeRange> timeRanges = dayPattern.times().stream().map(time ->
                                    new TimeRange(LocalTime.parse(time.startTime()), LocalTime.parse(time.endTime()))
                                ).toList();
                                return new DayPattern(timeRanges);
                            }).toList();
                            return new WeekPattern(days);
                        })
                        .toList();

                    wtps.computeIfAbsent(personId, k -> new ArrayList<>());
                    wtps.get(personId).add(
                        new WorkTimePattern(
                            personWorktimePattern.startDate() != null ? DateTimeService.fromStringDateToInstant(personWorktimePattern.startDate(), DateTimeService.START_DATE_FORMATTER) : null,
                            personWorktimePattern.endDate() != null ? DateTimeService.fromStringDateToInstant(personWorktimePattern.endDate(), DateTimeService.END_DATE_FORMATTER) : null,
                            weeks)
                    );
                }
            });
        return wtps;
    }
}
