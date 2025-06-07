package com.bigitcompany.cloudaireadmodel.aggregation.security;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;
import com.sap.fsm.optimization.permissions.model.AuthorizationResult;
import com.sap.fsm.springboot.starter.authentication.resourceserver.CloudOAuth2Principal;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class TenantAuthorizerService {

    private final TokenTypeChecker tokenTypeChecker;

    private final TracingService tracingService;

    private final PermissionService permissionService;

    public TenantAuthorizerService(TokenTypeChecker tokenTypeChecker, TracingService tracingService, PermissionService permissionService) {
        this.tokenTypeChecker = tokenTypeChecker;
        this.tracingService = tracingService;
        this.permissionService = permissionService;
    }

    @NewSpan("authorize-tenant\uD83C\uDF55")
    public AuthorizationResult isAuthorized(Tenant tenant) {
        tracingService.eventOnCurrentSpan("started");

        if (!tokenTypeChecker.isTokenTypeBearer()) {
            return new AuthorizationResult(false, "Authentication type is not 'Bearer token'");
        } else {
            var principal = getCloudOauth2Principal();
            if (principal == null) {
                return new AuthorizationResult(false, "SecurityContext/Principal is missing or invalid.");
            }
            return permissionService.getPermissionResult(tenant, principal);
        }
    }

    public CloudOAuth2Principal getCloudOauth2Principal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof CloudOAuth2Principal cloudOauth2Principal) {
            return cloudOauth2Principal;
        } else if (authentication != null && authentication.getPrincipal() instanceof CloudOAuth2Principal cloudOauth2Principal) {
            return cloudOauth2Principal;
        } else {
            return null;
        }
    }

    public TokenTypeChecker getTokenTypeChecker() {
        return this.tokenTypeChecker;
    }
}
