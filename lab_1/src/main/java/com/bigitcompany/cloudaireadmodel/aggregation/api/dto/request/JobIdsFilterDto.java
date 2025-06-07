package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record JobIdsFilterDto(@Schema(description = "List of jobIds that should be filtered") List<String> ids,
                              @Schema(description = "List of mandatory requirements the jobs will be filtered and partitioned on") List<String> requirementNames) {
}
