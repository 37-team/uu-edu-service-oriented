package com.bigitcompany.cloudaireadmodel.common.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Configuration {

    private static final boolean DEFAULT_INDEX_ENABLED = false;

    private static final boolean DEFAULT_QUERYING_ENABLED = false;

    private UUID id;

    private Instant lastChanged;

    private final Tenant tenant;

    private final boolean indexingEnabled;

    private final boolean queryingEnabled;

    /**
     * Constructor with default values
     */
    public Configuration(Tenant tenant) {
        this(tenant, DEFAULT_INDEX_ENABLED, DEFAULT_QUERYING_ENABLED);
    }

    /**
     * Constructor for querying and persisting
     */
    public Configuration(Tenant tenant,
                         Boolean indexingEnabled,
                         Boolean queryingEnabled) {
        this.tenant = tenant;
        this.indexingEnabled = indexingEnabled != null ? indexingEnabled : DEFAULT_INDEX_ENABLED;
        this.queryingEnabled = queryingEnabled != null ? queryingEnabled : DEFAULT_QUERYING_ENABLED;
    }

    public Configuration(com.bigitcompany.cloudaireadmodel.common.persistence.jpa.Configuration jpa) {
        id = jpa.getId();
        lastChanged = jpa.getLastChanged();
        tenant = new Tenant(jpa.getAccountId(), jpa.getCompanyId());
        indexingEnabled = jpa.isIndexingEnabled();
        queryingEnabled = jpa.isQueryingEnabled();
    }

    public UUID getId() {
        return id;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    public boolean isQueryingEnabled() {
        return queryingEnabled;
    }

    @Override
    @Generated
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return isIndexingEnabled() == that.isIndexingEnabled() && isQueryingEnabled() == that.isQueryingEnabled() && Objects.equals(getId(), that.getId()) && Objects.equals(getLastChanged(), that.getLastChanged()) && Objects.equals(getTenant(), that.getTenant());
    }

    @Override
    @Generated
    public int hashCode() {
        return Objects.hash(getId(), getLastChanged(), getTenant(), isIndexingEnabled(), isQueryingEnabled());
    }
}