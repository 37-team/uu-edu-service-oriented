package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.partnerdispatch;

import com.bigitcompany.cloudaireadmodel.common.connectors.cloud.CloudAsyncRepository;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class PartnerDispatchConnector extends CloudAsyncRepository<Map<UUID, List<UUID>>> {

    private static final BodyExtractor<Mono<Map<UUID, List<UUID>>>, ClientHttpResponse> responseExtractor =
        new ExcludeListsResponseExtractor();

    private static final String JOBS_BLACKLISTS_ENDPOINT = "/api/partner-dispatch/v1/blacklist";

    private final String resourceExcludeListAPI;

    public PartnerDispatchConnector(@Value("${service.partner-dispatch.host}") String partnerDispatchServiceHost, WebClient webClient) {
        super(webClient);
        resourceExcludeListAPI = partnerDispatchServiceHost + JOBS_BLACKLISTS_ENDPOINT;
    }

    public CompletableFuture<Map<UUID, List<UUID>>> fetchResourceExcludeList(List<UUID> jobIds) {
        var componentsBuilder = UriComponentsBuilder.fromUriString(resourceExcludeListAPI);
        jobIds.forEach(jobId -> componentsBuilder.queryParam("activityIds", UuidMapper.toFsmId(jobId)));
        var requestURI = componentsBuilder.build().toUri();

        return makeGetRequest(requestURI, responseExtractor);
    }
}
