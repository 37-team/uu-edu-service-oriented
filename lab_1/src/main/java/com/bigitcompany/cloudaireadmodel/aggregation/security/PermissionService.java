package com.bigitcompany.cloudaireadmodel.aggregation.security;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.sap.fsm.optimization.permissions.OptimizationPermissionChecker;
import com.sap.fsm.optimization.permissions.client.CloudMasterClient;
import com.sap.fsm.optimization.permissions.model.AuthorizationResult;
import com.sap.fsm.springboot.starter.authentication.resourceserver.CloudOAuth2Principal;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final CloudMasterClient cloudMasterClient;

    public PermissionService(CloudMasterClient cloudMasterClient) {
        this.cloudMasterClient = cloudMasterClient;
    }

    public AuthorizationResult getPermissionResult(Tenant tenant, CloudOAuth2Principal principal) {
        var principalWrapper = new CloudOAuth2PrincipalWrapper(principal);
        var optimizationPermissionChecker = new OptimizationPermissionChecker(cloudMasterClient, principalWrapper);
        return optimizationPermissionChecker.checkReadPermissions(tenant.getCompanyId(), RequestContext.getClientId(), RequestContext.getClientVersion());
    }
}
