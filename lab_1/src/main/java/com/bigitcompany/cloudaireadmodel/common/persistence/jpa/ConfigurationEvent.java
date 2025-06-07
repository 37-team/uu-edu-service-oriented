package com.bigitcompany.cloudaireadmodel.common.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity(name = ConfigurationEvent.CONFIGURATION_HISTORY_TABLE)
@Table(name = ConfigurationEvent.CONFIGURATION_HISTORY_TABLE, schema = "public")
public class ConfigurationEvent {

    public static final String CONFIGURATION_HISTORY_TABLE = "tenantconfigurationhistory";

    @Id
    @GeneratedValue
    @Column(name = "id", unique = true)
    private UUID id;

    @NotNull
    @CreatedDate
    @Column(nullable = false)
    private Instant createdDate;

    @NotNull
    @Column(nullable = false)
    private Long accountId;

    @NotNull
    @Column(nullable = false)
    private Long companyId;

    @NotNull
    @Column(nullable = false)
    private boolean indexingEnabled;

    @NotNull
    @Column(nullable = false)
    private boolean queryingEnabled;

    public ConfigurationEvent() {
        // Used by JPA
    }

    public ConfigurationEvent(@NotNull Long accountId, @NotNull Long companyId, boolean indexingEnabled, boolean queryingEnabled) {
        this.accountId = accountId;
        this.companyId = companyId;
        this.indexingEnabled = indexingEnabled;
        this.queryingEnabled = queryingEnabled;
        this.createdDate = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedDate() {
        return createdDate;
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
}