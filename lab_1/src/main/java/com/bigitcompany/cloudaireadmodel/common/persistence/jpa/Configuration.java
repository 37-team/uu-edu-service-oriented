package com.bigitcompany.cloudaireadmodel.common.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity(name = Configuration.TENANT_CONFIGURATION_TABLE)
@Table(name = Configuration.TENANT_CONFIGURATION_TABLE, schema = "public")
public class Configuration {

    public static final String TENANT_CONFIGURATION_TABLE = "tenantConfiguration";

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true)
    private UUID id;

    @Version
    @UpdateTimestamp
    private Instant lastChanged;

    @NotNull
    @Column(nullable = false)
    private Long accountId;

    @NotNull
    @Column(nullable = false)
    private Long companyId;

    private boolean indexingEnabled;

    private boolean queryingEnabled;

    public Configuration() {
        // Used by JPA
    }

    public Configuration(@NotNull Long accountId, @NotNull Long companyId, boolean indexingEnabled, boolean queryingEnabled) {
        this.accountId = accountId;
        this.companyId = companyId;
        this.indexingEnabled = indexingEnabled;
        this.queryingEnabled = queryingEnabled;
    }

    public UUID getId() {
        return id;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Tenant getTenant() {
        return new Tenant(getAccountId(), getCompanyId());
    }

    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    public boolean isQueryingEnabled() {
        return queryingEnabled;
    }

    // Manipulators
    public void setIndexingEnabled(boolean indexingEnabled) {
        this.indexingEnabled = indexingEnabled;
    }

    public void setQueryingEnabled(boolean queryingEnabled) {
        this.queryingEnabled = queryingEnabled;
    }
}