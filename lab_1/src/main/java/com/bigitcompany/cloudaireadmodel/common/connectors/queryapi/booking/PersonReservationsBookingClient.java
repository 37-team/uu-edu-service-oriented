package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Booking;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ADDRESS;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSONRESERVATION;

@Component
public class PersonReservationsBookingClient extends AbstractQueryApiClient {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(PERSONRESERVATION, ADDRESS, PERSON);

    protected PersonReservationsBookingClient(@Value("${service.query-api.host}") String queryApiHost,
                                              @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<Booking>>> queryPersonReservationsAsBookings(ReadModelRequestContext requestContext,
                                                                                         List<UUID> resourceIds,
                                                                                         BookingsFilter filter) {
        var body = createQueryBody(resourceIds, filter);
        List<PersonReservationsBookingDto> pages = getAllPages(requestContext, REQUEST_DTOS, body, PersonReservationsBookingDto.class);
        return CompletableFuture.completedFuture(unwrapPagesByPersonId(pages));
    }

    private Map<String, String> createQueryBody(List<UUID> resourceIds, BookingsFilter filter) {
        var sqlSelect = """
                        SELECT DISTINCT\s
                            person.id,\s
                            personReservation.id,\s
                            personReservation.startDate,\s
                            personReservation.endDate,\s
                            personReservation.exclusive,\s
                            address.location\s
                        FROM Person person\s
                            INNER JOIN PersonReservation personReservation ON person.id IN personReservation.persons\s
                            LEFT JOIN Address address ON address.id=personReservation.address\s
                        WHERE person.id IN ('%s')\s                    
                            AND personReservation.exclusive = true\s
                            AND personReservation.endDate > '%s'\s
                            AND personReservation.startDate < '%s'\s
                            ORDER BY person.id ASC, personReservation.id ASC
                            
            """.formatted(
                String.join("','", resourceIds.stream().map(UuidMapper::toFsmId).toList()),
                DateTimeService.fromInstantToISOStringNoMilliseconds(filter.getEarliest()),
                DateTimeService.fromInstantToISOStringNoMilliseconds(filter.getLatest()));
        return Map.of("query", sqlSelect);
    }

    private Map<UUID, List<Booking>> unwrapPagesByPersonId(List<PersonReservationsBookingDto> pages) {
        return pages.stream()
            .flatMap(personReservationDto -> personReservationDto.getData().stream())
            .map(personReservationData -> {
                var personId = personReservationData.person().id();
                var location = (personReservationData.address() != null && personReservationData.address().location() != null) ?
                    new Location(personReservationData.address().location().latitude(), personReservationData.address().location().longitude()) : null;
                var personReservation = personReservationData.personReservation();
                return new BookingQueryApi(DateTimeService.toInstant(personReservation.startDate()), DateTimeService.toInstant(personReservation.endDate()), location, null, personReservation.exclusive(), UuidMapper.toUUID(personId));
            }).collect(Collectors.groupingBy(BookingQueryApi::personId, Collectors.mapping(queryApiBooking ->
                new Booking.Builder()
                    .start(queryApiBooking.start())
                    .end(queryApiBooking.end())
                    .location(queryApiBooking.location())
                    .job(queryApiBooking.job())
                    .exclusive(queryApiBooking.exclusive())
                    .build(), Collectors.toList())
            ));
    }
}
