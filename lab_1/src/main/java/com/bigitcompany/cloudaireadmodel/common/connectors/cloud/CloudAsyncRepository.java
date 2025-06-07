package com.bigitcompany.cloudaireadmodel.common.connectors.cloud;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import static com.bigitcompany.cloudaireadmodel.aggregation.domain.services.RequestHeaderProvider.requestHeaders;

public abstract class CloudAsyncRepository<T> {

    private static final Logger LOG = Loggers.getLogger(MethodHandles.lookup().lookupClass());

    private static final ParameterizedTypeReference<String> STRING_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;

    protected CloudAsyncRepository(WebClient webClient) {
        this.webClient = webClient;
    }

    protected WebClient.RequestHeadersUriSpec<?> requestGetMethodSpecification() {
        var request = webClient.get();
        request.accept(MediaType.APPLICATION_JSON);
        requestHeaders().forEach(request::header);
        return request;
    }

    protected CompletableFuture<T> makeGetRequest(URI requestURI, BodyExtractor<Mono<T>, ClientHttpResponse> responseExtractor) {
        return requestGetMethodSpecification().uri(requestURI)
            .exchangeToMono(response -> {
                if (response.statusCode().isError() || response.statusCode().is3xxRedirection() || response.statusCode().is1xxInformational()) {
                    response.bodyToMono(STRING_PARAMETERIZED_TYPE_REFERENCE).doOnNext(message -> LOG.error("Wrong status code " + message));
                }
                return response.body(responseExtractor);
            })
            .doOnError(throwable -> LOG.error("Exception occurred while making request", throwable))
            .toFuture();
    }

    protected WebClient.RequestBodyUriSpec requestPostMethodSpecification() {
        var request = webClient.post();
        request.accept(MediaType.APPLICATION_JSON);
        request.contentType(MediaType.APPLICATION_JSON);
        requestHeaders().forEach(request::header);
        return request;
    }

    protected Mono<T> makePostRequest(URI requestURI, String body, BodyExtractor<Mono<T>, ClientHttpResponse> responseExtractor) {
        return requestPostMethodSpecification().uri(requestURI)
            .bodyValue(body)
            .exchangeToMono(response -> {
                if (response.statusCode().isError() || response.statusCode().is3xxRedirection() || response.statusCode().is1xxInformational()) {
                    response.bodyToMono(STRING_PARAMETERIZED_TYPE_REFERENCE).doOnNext(message -> LOG.error("Wrong status code " + message));
                }
                return response.body(responseExtractor);
            })
            .doOnError(throwable -> LOG.error("Exception occurred while making request", throwable));
    }
}
