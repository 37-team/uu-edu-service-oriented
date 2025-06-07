package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.partnerdispatch;

import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.BodyExtractors;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ExcludeListsResponseExtractor implements BodyExtractor<Mono<Map<UUID, List<UUID>>>, ClientHttpResponse> {

    private static final ParameterizedTypeReference<Map<String, List<String>>> STRING_OBJECT_MAP = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<Map<UUID, List<UUID>>> extract(ClientHttpResponse inputMessage, Context context) {

        BodyExtractor<Mono<Map<String, List<String>>>, ReactiveHttpInputMessage> delegate = BodyExtractors
            .toMono(STRING_OBJECT_MAP);

        return delegate.extract(inputMessage, context)
            .map(ExcludeListsResponseExtractor::stringsToFsmID)
            .onErrorMap(ex -> new IllegalArgumentException("An error occurred parsing the exclude-lists response: " + ex.getMessage(), ex))
            .switchIfEmpty(Mono.error(() -> new RuntimeException("Empty exclude-lists response")))
            .onErrorReturn(Collections.emptyMap());
    }

    private static Map<UUID, List<UUID>> stringsToFsmID(Map<String, List<String>> stringListMap) {
        Map<UUID, List<UUID>> uuidListMap = new HashMap<>();
        stringListMap.forEach((key, value) -> uuidListMap.put(UuidMapper.toUUID(key), UuidMapper.toUUIDs(value)));
        return uuidListMap;
    }
}