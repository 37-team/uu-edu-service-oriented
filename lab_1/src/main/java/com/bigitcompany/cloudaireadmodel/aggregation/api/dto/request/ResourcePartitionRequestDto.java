package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record ResourcePartitionRequestDto(
        @Schema(description = "List of mandatory skill persons will be partitioned on")
        @NotNull(message = "ids must not be null")
        @NotEmpty(message = "ids must not be empty") List<String> ids,

        @Schema(description = "List of resourceIds that should be partitioned")
        @NotNull(message = "skillNames must exist")
        @NotEmpty(message = "skillNames must be not empty") List<String> skillNames
) {

    // For Jackson
    public ResourcePartitionRequestDto() {
        this(List.of(), List.of());
    }
}
