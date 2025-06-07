package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ConfigurationResponseDto {

    private UUID id;

    private Instant lastChanged;

    private Long accountId;

    private Long companyId;

    private boolean indexingEnabled;

    private boolean queryingEnabled;

    @JsonCreator
    public ConfigurationResponseDto(UUID id,
                                    Instant lastChanged,
                                    Long accountId,
                                    Long companyId,
                                    boolean indexingEnabled,
                                    boolean queryingEnabled) {
        this.id = id;
        this.lastChanged = lastChanged;
        this.accountId = accountId;
        this.companyId = companyId;
        this.indexingEnabled = indexingEnabled;
        this.queryingEnabled = queryingEnabled;
    }

    public ConfigurationResponseDto() {
        // Used by jackson
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

    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    public boolean isQueryingEnabled() {
        return queryingEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationResponseDto that = (ConfigurationResponseDto) o;
        return isIndexingEnabled() == that.isIndexingEnabled() && isQueryingEnabled() == that.isQueryingEnabled() && Objects.equals(getId(), that.getId()) && Objects.equals(getLastChanged(), that.getLastChanged()) && Objects.equals(getAccountId(), that.getAccountId()) && Objects.equals(getCompanyId(), that.getCompanyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLastChanged(), getAccountId(), getCompanyId(), isIndexingEnabled(), isQueryingEnabled());
    }

    @Override
    public String toString() {
        return "ConfigurationResponseDto{" +
            "id=" + id +
            ", lastChanged=" + lastChanged +
            ", accountId=" + accountId +
            ", companyId=" + companyId +
            ", indexingEnabled=" + indexingEnabled +
            ", queryingEnabled=" + queryingEnabled +
            '}';
    }
}