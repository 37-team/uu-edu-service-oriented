package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ResourcePartitionDto(
        @Schema(description = "Skill (tag) name") @JsonProperty String skillName,
        @Schema(description = "List of resourceIds belonging to the partition") @JsonProperty List<String> ids) { }
