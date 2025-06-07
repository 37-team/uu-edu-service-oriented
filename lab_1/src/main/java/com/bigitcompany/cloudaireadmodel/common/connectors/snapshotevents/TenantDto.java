package com.bigitcompany.cloudaireadmodel.common.connectors.snapshotevents;


import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;

public class TenantDto {

    private final String accountId;

    private final String companyId;

    public TenantDto(Tenant tenant) {
        accountId = String.valueOf(tenant.getAccountId());
        companyId = String.valueOf(tenant.getCompanyId());
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCompanyId() {
        return companyId;
    }
}