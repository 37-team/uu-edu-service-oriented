package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaderProvider {


    private RequestHeaderProvider() {
    }

    public static Map<String, String> requestHeaders() {

        var token = authorizationToken();

        Map<String, String> headers = new HashMap<>();

        headers.put("Authorization", "Bearer " + token);
        headers.put("X-Account-Id", String.valueOf(RequestContext.getAccountId()));
        headers.put("X-Account-Name", RequestContext.getAccountName());
        headers.put("X-Company-Id", String.valueOf(RequestContext.getCompanyId()));
        headers.put("X-Company-Name", RequestContext.getCompanyName());
        headers.put("x-client-id", RequestContext.getClientId());
        headers.put("x-client-version", RequestContext.getClientVersion());

        return headers;

    }

    private static String authorizationToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof BearerTokenAuthentication) {

            BearerTokenAuthentication tokenAuthentication = (BearerTokenAuthentication) authentication;
            String token = tokenAuthentication.getToken().getTokenValue();

            if (token.toLowerCase().contains("bearer")) {

                return token.substring("bearer".length()).trim();

            }

            return token;

        }

        throw new IllegalArgumentException("Authentication should be instance of " + BearerTokenAuthentication.class);

    }
}