package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.skill;

import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ProficiencyLevelResponseExtractor implements BodyExtractor<Mono<Map<UUID, Integer>>, ClientHttpResponse> {

    private static final ParameterizedTypeReference<SkillProficiencyResponseDto> STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<Map<UUID, Integer>> extract(ClientHttpResponse inputMessage, Context context) {
        BodyExtractor<Mono<SkillProficiencyResponseDto>, ReactiveHttpInputMessage> delegate = BodyExtractors.toMono(STRING_OBJECT_MAP);
        return delegate.extract(inputMessage, context)
            .onErrorMap(ex -> new IllegalArgumentException("An error occurred parsing the Skills Microservice response: " + ex.getMessage(), ex))
            .switchIfEmpty(Mono.error(() -> new RuntimeException("Empty Response")))
            .map(ProficiencyLevelResponseExtractor::parse)
            .onErrorReturn(Collections.emptyMap());
    }

    private static Map<UUID, Integer> parse(SkillProficiencyResponseDto responseDto) {
        Map<UUID, Integer> proficiencyMap = new HashMap<>();
        responseDto.getContent().forEach(proficiencyDto ->
            proficiencyMap.put(UuidMapper.toUUID(proficiencyDto.getId()), proficiencyDto.getProficiencyLevel())
        );

        return proficiencyMap;
    }
}