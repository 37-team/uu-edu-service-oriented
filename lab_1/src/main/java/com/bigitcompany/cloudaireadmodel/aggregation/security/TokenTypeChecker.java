package com.bigitcompany.cloudaireadmodel.aggregation.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

@Service
public class TokenTypeChecker {

    public boolean isTokenTypeBearer() {
        return SecurityContextHolder.getContext().getAuthentication() instanceof BearerTokenAuthentication;
    }
}
