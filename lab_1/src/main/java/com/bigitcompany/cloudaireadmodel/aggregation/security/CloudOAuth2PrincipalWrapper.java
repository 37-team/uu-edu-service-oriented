package com.bigitcompany.cloudaireadmodel.aggregation.security;

import com.sap.fsm.optimization.permissions.model.PermissionsPrincipal;
import com.sap.fsm.springboot.starter.authentication.resourceserver.CloudOAuth2Principal;

public class CloudOAuth2PrincipalWrapper implements PermissionsPrincipal {
    public static final String SERVICE_AUTHORITY = "SERVICE";
    private final CloudOAuth2Principal principal;

    public CloudOAuth2PrincipalWrapper(CloudOAuth2Principal principal) {
        this.principal = principal;
    }

    @Override
    public Long getPermissionGroupId(Long companyId) {
        return principal.getPermissionGroupId(companyId);
    }

    @Override
    public Long getAccountId() {
        return principal.getAccountId();
    }

    @Override
    public boolean hasServiceAuthorities() {
        return principal.getAuthorities().stream().anyMatch(authority -> SERVICE_AUTHORITY.equals(authority.getAuthority()));
    }
}
