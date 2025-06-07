package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ConfigurationResponseDto;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration;

public class ConfigurationMapper {

    private ConfigurationMapper() {
    }

    public static ConfigurationResponseDto toConfigurationResponseDto(Configuration configuration) {
        return new ConfigurationResponseDto(
            configuration.getId(),
            configuration.getLastChanged(),
            configuration.getTenant().getAccountId(),
            configuration.getTenant().getCompanyId(),
            configuration.isIndexingEnabled(),
            configuration.isQueryingEnabled()
        );
    }
}