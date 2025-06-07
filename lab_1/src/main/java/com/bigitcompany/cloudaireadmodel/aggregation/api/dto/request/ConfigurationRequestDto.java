package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

public class ConfigurationRequestDto {

    private Boolean indexingEnabled;

    private Boolean queryingEnabled;

    public ConfigurationRequestDto(Boolean indexingEnabled, Boolean queryingEnabled) {
        this.indexingEnabled = indexingEnabled;
        this.queryingEnabled = queryingEnabled;
    }

    public ConfigurationRequestDto() {
        // required by jackson
    }

    public Boolean isIndexingEnabled() {
        return indexingEnabled;
    }

    public Boolean isQueryingEnabled() {
        return queryingEnabled;
    }
}