package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.holiday;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.DayType;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Holiday;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class YearlyCalendarResponseExtractor implements BodyExtractor<Mono<Map<UUID, List<Holiday>>>, ClientHttpResponse> {

    private static final ParameterizedTypeReference<Map<String, List<HolidayDto>>> STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<Map<UUID, List<Holiday>>> extract(ClientHttpResponse inputMessage, Context context) {
        BodyExtractor<Mono<Map<String, List<HolidayDto>>>, ReactiveHttpInputMessage> delegate = BodyExtractors
            .toMono(STRING_OBJECT_MAP);
        return delegate.extract(inputMessage, context)
            .onErrorMap(ex -> new IllegalArgumentException("An error occurred parsing the Holidays response: " + ex.getMessage(), ex))
            .switchIfEmpty(Mono.error(() -> new RuntimeException("Empty Holidays Response")))
            .map(YearlyCalendarResponseExtractor::parse)
            .onErrorReturn(Collections.emptyMap());
    }

    private static Map<UUID, List<Holiday>> parse(Map<String, List<HolidayDto>> stringObjectMap) {
        Map<UUID, List<Holiday>> holidays = new HashMap<>();
        for (Map.Entry<String, List<HolidayDto>> personHolidayDto : stringObjectMap.entrySet()) {
            holidays.put(
                UuidMapper.toUUID(personHolidayDto.getKey()),
                personHolidayDto.getValue()
                    .stream().map(dto ->
                        new Holiday(
                                LocalDate.parse(dto.getDate().substring(0, dto.getDate().indexOf('T'))),
                                dto.getHalfDayType() != null ? DayType.valueOf(dto.getHalfDayType()) : DayType.FULL_DAY_OFF
                        )
                    ).collect(Collectors.toList())
            );
        }
        return holidays;
    }
}