package com.bigitcompany.cloudaireadmodel.common.domain.model;

import java.util.Objects;
import java.util.Optional;

public class Tenant {

    private final Long accountId;
    private String accountName;

    private final Long companyId;
    private String companyName;

    public Tenant(Long accountId, Long companyId) {
        this.accountId = accountId;
        this.companyId = companyId;
    }

    public Tenant(Long accountId, String accountName, Long companyId, String companyName) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.companyId = companyId;
        this.companyName = companyName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getSchema() {
        return "a" + accountId + "_" + companyId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var tenant = (Tenant) o;
        return Objects.equals(getAccountId(), tenant.getAccountId()) && Objects.equals(getCompanyId(), tenant.getCompanyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountId(), getCompanyId());
    }

    @Override
    public String toString() {
        return "Tenant{" +
            "accountId=" + accountId +
            ", accountName=" + Optional.of(accountName).orElse("NONE") +
            ", companyId=" + companyId +
            ", companyName=" + Optional.of(companyName).orElse("NONE") +
            '}';
    }
}