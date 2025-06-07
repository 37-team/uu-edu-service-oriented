package com.bigitcompany.cloudaireadmodel.common.connectors.snapshotevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service
public class TechnicalEventsServiceConnector {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String DOMAIN = "data";
    private static final String TOPIC = "snapshot-events-cloud-ai-read-model";
    @SuppressWarnings("java:S1075") // URIs should not be hardcoded: not relevant for API paths
    private static final String API_PATH = "/v1/snapshot-events-group";

    private final WebClient webClient;
    private final String url;
    private final String username;
    private final String password;

    public TechnicalEventsServiceConnector(
        WebClient webClient,
        @Value("${service.techevents.url}") String url,
        @Value("${service.techevents.username}") String username,
        @Value("${service.techevents.password}") String password
    ) {
        if (url == null || username == null || password == null) {
            LOG.error("[TechnicalEventsServiceConnector] mandatory configuration properties are missing");
            throw new DomainResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Snapshot events have not been configured correctly.");
        }

        this.webClient = webClient;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void enableSnapshotEventsForTenant(Tenant tenant) {
        LOG.debug("Enabling snapshot events for accountID: {} and companyID: {}", tenant.getAccountId(), tenant.getCompanyId());
        final var techEventUri = fromHttpUrl(url).path(API_PATH).build().toUri();

        ResponseEntity<String> response = webClient.post()
            .uri(techEventUri).accept(MediaType.APPLICATION_JSON)
            .headers(createHeaders(tenant))
            .bodyValue(createBody(tenant))
            .retrieve()
            .toEntity(String.class)
            .block();

        if (Objects.requireNonNull(response).getStatusCode() == HttpStatus.ACCEPTED || Objects.requireNonNull(response).getStatusCode() == HttpStatus.CREATED) {
            LOG.info("Requested snapshot for accountId {} and companyId {} successfully!", tenant.getAccountId(), tenant.getCompanyId());
        } else {
            LOG.error("Requesting snapshot for accountId {} and companyId {} failed! Techevents response {}: {} ", tenant.getAccountId(), tenant.getCompanyId(), response.getStatusCode(), response.getBody());
        }
    }

    private String createBody(Tenant tenant) {
        try {
            List<RequestItemDto> items = Arrays.stream(SupportedFsmDtos.values())
                    .map(SupportedFsmDtos::getName)
                    .map(RequestItemDto::new)
                    .toList();

            List<TenantDto> tenants = Collections.singletonList(new TenantDto(tenant));
            var requestDto = new RequestDto(DOMAIN, tenants, items, TOPIC);
            var objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(requestDto);

        } catch (Exception error) {
            LOG.error("Unexpected error while building request to snapshot event service.", error);
            throw new DomainResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Initiating data snapshot was unsuccessful, please open a support ticket.");
        }
    }

    private Consumer<HttpHeaders> createHeaders(Tenant tenant) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", createAuthHeader());
        headers.add("x-account-id", tenant.getAccountId().toString());
        headers.add("x-company-id", tenant.getCompanyId().toString());
        return header -> header.addAll(headers);
    }

    private String createAuthHeader() {
        var authString = String.format("%s:%s", username, password);
        return String.format("Basic %s", Base64.getEncoder().encodeToString(authString.getBytes()));
    }
}
