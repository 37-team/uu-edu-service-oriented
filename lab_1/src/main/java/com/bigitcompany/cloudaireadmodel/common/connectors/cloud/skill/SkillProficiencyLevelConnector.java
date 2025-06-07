package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.skill;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.CloudAsyncRepository;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ChunkMaker;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class SkillProficiencyLevelConnector extends CloudAsyncRepository<Map<UUID, Integer>> {

    private static final BodyExtractor<Mono<Map<UUID, Integer>>, ClientHttpResponse> responseExtractor =
        new ProficiencyLevelResponseExtractor();

    private final String skillServiceHost;

    private static final String SKILL_SEARCH_PATH = "/api/v1/skills/search";

    private static final  String REQUIREMENT_SEARCH_PATH = "/api/v1/requirements/search";

    private static final int CHUNK_SIZE = 500;

    private final ChunkMaker chunkMaker;

    protected SkillProficiencyLevelConnector(@Value("${service.skill.host}") String skillServiceHost,
                                             @Qualifier("cloudWebClient") WebClient  webClient) {
        super(webClient);
        this.skillServiceHost = skillServiceHost;
        this.chunkMaker = new ChunkMaker(CHUNK_SIZE);
    }



    private Map<UUID, Integer> fetchProficiencies(List<UUID> ids, String path) {
        Map<UUID, Integer> proficiencies = new HashMap<>();
        var chunkedIds = chunkMaker.breakIntoChunks(ids);

        chunkedIds.forEach(chunk -> {

            var requestURI = UriComponentsBuilder.fromUriString(skillServiceHost + path)
                .build().toUri();

            Map<String, Object> filter = new HashMap<>();
            filter.put("field", "id");
            filter.put("operator", "in");
            filter.put("value", ids.stream().map(UuidMapper::toFsmId).toList());

            Map<String, Object> body = new HashMap<>();
            body.put("filter", List.of(filter));
            body.put("paginated", false);

            String jsonBody;
            try {
                jsonBody = new ObjectMapper().writeValueAsString(body);
            } catch (JsonProcessingException e) {
                throw new DomainException("Error parsing proficiency request body:", e);
            }

            proficiencies.putAll(Objects.requireNonNull(makePostRequest(requestURI, jsonBody, responseExtractor).block()));
        });
        return proficiencies;
    }

    public Map<UUID, Integer> fetchSkillsProficiencies(List<UUID> ids) {
       return fetchProficiencies(ids, SKILL_SEARCH_PATH);
    }

    public Map<UUID, Integer> fetchRequirementsProficiencies(List<UUID> ids) {
        return fetchProficiencies(ids, REQUIREMENT_SEARCH_PATH);
    }

}
