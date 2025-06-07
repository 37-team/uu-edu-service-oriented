package com.bigitcompany.cloudaireadmodel.common.domain.model;

import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;

public record ReadModelRequestContext(String accountId, String accountName, String companyId, String companyName,
                                      String clientId, String clientVersion) {
    public ReadModelRequestContext() {
        this(
            String.valueOf(RequestContext.getAccountId()),
            RequestContext.getAccountName(),
            String.valueOf(RequestContext.getCompanyId()),
            RequestContext.getCompanyName(),
            RequestContext.getClientId(),
            RequestContext.getClientVersion()
        );
    }
}