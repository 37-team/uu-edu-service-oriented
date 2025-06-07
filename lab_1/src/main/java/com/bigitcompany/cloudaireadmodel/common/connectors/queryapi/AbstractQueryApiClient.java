package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi;

import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractQueryApiClient {

    @SuppressWarnings("java:S1075")
    private static final String QUERY_API_PATH = "/api/query/v1";

    private final Logger logger;

    private final int queryApiPageSize;

    protected String fullQueryApiUrl;

    @Autowired
    protected RestTemplate serviceRestTemplate;

    @Autowired
    protected RestTemplate userRestTemplate;

    protected AbstractQueryApiClient(@Value("${service.query-api.host}") String queryApiHost,
                                     @Value("${service.query-api.page-size}") int queryApiPageSize,
                                     Logger logger) {
        fullQueryApiUrl = queryApiHost + QUERY_API_PATH;
        this.queryApiPageSize = queryApiPageSize;
        this.logger = logger;
    }

    protected <P extends AbstractPage> List<P> getAllPages(ReadModelRequestContext requestContext, Set<SupportedFsmDtos> dtoVersions, Map<String, String> body, Class<P> responseType) {
        List<P> allPages = new ArrayList<>();

        var page = fetchFirstPage(requestContext, dtoVersions, body, responseType);
        allPages.add(page);
        while (page.getCurrentPage() < page.getLastPage()) {
            page = makeQueryApiPostRequest(requestContext, dtoVersions, body, responseType, page.getCurrentPage() + 1);
            allPages.add(page);
        }

        return allPages;
    }

    protected <P extends AbstractPage> P fetchFirstPage(ReadModelRequestContext requestContext,
                                                        Set<SupportedFsmDtos> dtoVersions,
                                                        Map<String, String> body,
                                                        Class<P> responseType) {
        return makeQueryApiPostRequest(requestContext, dtoVersions, body, responseType, 1);
    }

    protected <P extends AbstractPage> P fetchFirstPageWithLimit(ReadModelRequestContext requestContext,
                                                                 Set<SupportedFsmDtos> dtoVersions,
                                                                 Map<String, String> body,
                                                                 Class<P> responseType,
                                                                 int pageSizeAsLimit) {
        return makeQueryApiPostRequest(requestContext, dtoVersions, body, responseType, 1, pageSizeAsLimit);
    }

    @NotNull
    private <P extends AbstractPage> P makeQueryApiPostRequest(ReadModelRequestContext requestContext,
                                                               Set<SupportedFsmDtos> dtoVersions,
                                                               Map<String, String> body,
                                                               Class<P> responseType,
                                                               int pageNumber){
        return makeQueryApiPostRequest(requestContext, dtoVersions, body, responseType, pageNumber, queryApiPageSize);
    }

    @NotNull
    private <P extends AbstractPage> P makeQueryApiPostRequest(ReadModelRequestContext requestContext,
                                                               Set<SupportedFsmDtos> dtoVersions,
                                                               Map<String, String> body,
                                                               Class<P> responseType,
                                                               int pageNumber,
                                                               int pageSize) {
        var requestResponseIdentifier = UUID.randomUUID().toString().substring(0, 8);
        var uri = createUri(requestContext, dtoVersions, pageNumber, pageSize);
        var headers = new HttpHeaders();
        headers.add(RequestContext.HEADER_CLIENT_ID, requestContext.clientId());
        headers.add(RequestContext.HEADER_CLIENT_VERSION, requestContext.clientVersion());
        var request = new HttpEntity<>(body, headers); // Other headers are set via UserClientHttpRequestInterceptor from starter-auth
        try {
            logQueryApiRequest(requestResponseIdentifier, dtoVersions, body.get("query"));
            var response = identifyAndGetRestTemplate().postForEntity(uri, request, responseType);
            var page = response.getBody();
            if (!response.hasBody() || page == null) {
                var message = String.format("Success response (%s) received from query API but response has no data for page %d", response.getStatusCode(), pageNumber);
                logger.error(message);
                throw new DomainResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred while fetching data for optimization");
            }
            logQueryApiResponse(requestResponseIdentifier, responseType, page, dtoVersions);
            return page;
        } catch (HttpClientErrorException clientResponseException) {
            var message = String.format("Unexpected query API response [%s] received while fetching page %d", clientResponseException.getStatusCode(), pageNumber);
            message += ": " + clientResponseException.getMessage();
            logger.error(message);
            throw new DomainResponseException(HttpStatus.valueOf(clientResponseException.getStatusCode().value()) , "Unexpected error occurred while fetching data for optimization: " + clientResponseException.getMessage());
        } catch (HttpServerErrorException | UnknownHttpStatusCodeException serverErrorException) {
            var message = String.format("Unexpected query API response [%s] received while fetching page %d", serverErrorException.getStatusCode(), pageNumber);
            message += ": " + serverErrorException.getMessage();
            logger.error(message);
            throw new DomainResponseException(HttpStatus.BAD_GATEWAY, "Unexpected error occurred while fetching data for optimization");
        }
    }

    private RestTemplate identifyAndGetRestTemplate() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ACCOUNT")) ||
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("CUSTOMER")) ||
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("RESTRICTED")) ||
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("USER"))) {
            logger.debug("Using userRestTemplate");
            return userRestTemplate;
        }
        logger.debug("Using serviceRestTemplate");
        return serviceRestTemplate;
    }

    private URI createUri(ReadModelRequestContext requestContext, Set<SupportedFsmDtos> dtoVersions, int page, int pageSize) {
        return UriComponentsBuilder.fromUriString(fullQueryApiUrl)
            .queryParam("dtos", dtoVersions.stream().map(SupportedFsmDtos::getFsmDtoVersion).collect(Collectors.joining(";")))
            .queryParam("clientIdentifier", "cloud-ai-read-model")
            .queryParam("account", requestContext.accountName())
            .queryParam("company", requestContext.companyName())
            .queryParam("page", page)
            .queryParam("pageSize", pageSize)
            .build().toUri();
    }

    private void logQueryApiRequest(String requestResponseIdentifier, Set<SupportedFsmDtos> dtos, String requestQuery){
        var template = "[QueryAPI] REQUEST %s [dtos=%s]: %s";
        var message = String.format(template, requestResponseIdentifier, dtos, requestQuery);
        logger.info(message);
    }

    private <P extends AbstractPage> void logQueryApiResponse(String requestResponseIdentifier, Class<P> responseType, P page, Set<SupportedFsmDtos> dtoVersions){
        var template = "[QueryAPI] RESPONSE %s [dtos=%s]: Page %d of %d with response type '%s'; %d object(s) returned;";
        var dtos = String.join(",", dtoVersions.stream().map(SupportedFsmDtos::getFsmDtoVersion).toList());
        var message = String.format(template,
            requestResponseIdentifier,
            dtos,
            page.getCurrentPage(),
            page.getLastPage(),
            responseType.getSimpleName(),
            page.getTotalObjectCount());
        logger.info(message);
    }
}
