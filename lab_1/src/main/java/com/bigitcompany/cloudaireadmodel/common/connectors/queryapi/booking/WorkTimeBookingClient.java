package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.WORKTIME;

@Component
public class WorkTimeBookingClient extends AbstractQueryApiClient {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(PERSON, WORKTIME);

    protected WorkTimeBookingClient(@Value("${service.query-api.host}") String queryApiHost,
                                    @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<Booking>>> queryWorkTimesAsBookings(ReadModelRequestContext requestContext,
                                                                                         List<UUID> resourceIds,
                                                                                         BookingsFilter filter) {
        var body = createQueryBody(resourceIds, filter);
        List<WorkTimeBookingDto> pages = getAllPages(requestContext, REQUEST_DTOS, body, WorkTimeBookingDto.class);
        return CompletableFuture.completedFuture(unwrapPagesByPersonId(pages));
    }

    private Map<String, String> createQueryBody(List<UUID> resourceIds, BookingsFilter filter) {
        var sqlSelect = """
                        SELECT DISTINCT\s
                            person.id,\s
                            workTime.id,\s
                            workTime.startDateTime,\s
                            workTime.endDateTime\s                           
                        FROM Person person,\s
                             WorkTime workTime\s              
                        WHERE person.id IN ('%s')\s                    
                            AND person.id = workTime.person\s
                            AND workTime.endDateTime > '%s'\s
                            AND workTime.startDateTime < '%s'\s
                            ORDER BY person.id ASC, workTime.id ASC
                            
            """.formatted(
                String.join("','", resourceIds.stream().map(UuidMapper::toFsmId).toList()),
                DateTimeService.fromInstantToISOStringNoMilliseconds(filter.getEarliest()),
                DateTimeService.fromInstantToISOStringNoMilliseconds(filter.getLatest()));
        return Map.of("query", sqlSelect);
    }

    private Map<UUID, List<Booking>> unwrapPagesByPersonId(List<WorkTimeBookingDto> pages) {
        Map<UUID, List<Booking>> workTimes = new HashMap<>();
        pages.stream()
            .flatMap(workTimeBookingDto -> workTimeBookingDto.getData().stream())
            .forEach(workTimeData -> {
                var personId = UuidMapper.toUUID(workTimeData.person().id());
                var workTime = workTimeData.workTime();

                workTimes.computeIfAbsent(personId, k -> new ArrayList<>());
                workTimes.get(personId).add(
                    new Booking.Builder()
                        .start(DateTimeService.toInstant(workTime.startDateTime()))
                        .end(DateTimeService.toInstant(workTime.endDateTime()))
                        .location(null)
                        .job(null)
                        .exclusive(true)
                        .build()
                    );
            });
        return workTimes;
    }
}
