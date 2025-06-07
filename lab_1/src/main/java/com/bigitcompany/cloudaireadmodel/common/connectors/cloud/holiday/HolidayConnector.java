package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.holiday;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Holiday;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.CloudAsyncRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class HolidayConnector extends CloudAsyncRepository<Map<UUID, List<Holiday>>> {

    private static final BodyExtractor<Mono<Map<UUID, List<Holiday>>>, ClientHttpResponse> responseExtractor =
        new YearlyCalendarResponseExtractor();

    private static final String HOLIDAY_API = "/api/v1/holiday/yearly-calendars";

    private final String holidayYearlyCalendarsUrl;

    public HolidayConnector(@Value("${service.holiday.host}") String holidayCalendarHost,
                            @Qualifier("cloudWebClient") WebClient webClient) {
        super(webClient);
        holidayYearlyCalendarsUrl = holidayCalendarHost + HOLIDAY_API;
    }

    public CompletableFuture<Map<UUID, List<Holiday>>> fetchHolidays(List<UUID> technicianIds, Instant earliestBooking, Instant latestBooking) {
        var requestURI = UriComponentsBuilder.fromUriString(holidayYearlyCalendarsUrl)
            .queryParam("technicians", technicianIds)
            .queryParam("fromDateTime", earliestBooking.toString())
            .queryParam("toDateTime", latestBooking.toString())
            .build().toUri();

        return makeGetRequest(requestURI, responseExtractor);
    }
}