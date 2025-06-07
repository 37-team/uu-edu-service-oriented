package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.security.TenantAuthorizerService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import org.springframework.http.HttpStatus;

public class ApiAccessController {

    private final ActiveTenantProvider tenantProvider;

    protected final TenantAuthorizerService tenantAuthorizerService;

    public ApiAccessController(TenantAuthorizerService tenantAuthorizerService, ActiveTenantProvider tenantProvider) {
        this.tenantAuthorizerService = tenantAuthorizerService;
        this.tenantProvider = tenantProvider;
    }

    public void ensureCorrectApiAccess(Tenant tenant, boolean proxyMode, String clientId, String clientVersion) {

        // common
        if (!tenantAuthorizerService.getTokenTypeChecker().isTokenTypeBearer()) {
            throw new DomainResponseException(HttpStatus.FORBIDDEN, "Authentication type is not 'Bearer token'");
        }

        // proxy mode specific checks
        if (proxyMode) {
            if (clientId == null || clientVersion == null) {
                throw new DomainResponseException(HttpStatus.BAD_REQUEST, "x-client-id and/or x-client-version request header not provided but required in proxy mode");
            }

            // read model specific checks
        } else {

            var authenticationResult = tenantAuthorizerService.isAuthorized(tenant);
            if (!authenticationResult.isAuthorized()) {
                throw new DomainResponseException(HttpStatus.FORBIDDEN, authenticationResult.getReason());
            }

            if (!tenantProvider.isTenantEnabled(tenant)) {
                throw new DomainResponseException(HttpStatus.FORBIDDEN, "Read model is not enabled for this tenant");
            }
        }
    }
}
