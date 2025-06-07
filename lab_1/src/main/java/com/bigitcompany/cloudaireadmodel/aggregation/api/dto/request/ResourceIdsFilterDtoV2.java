package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public record ResourceIdsFilterDtoV2(
        @JsonProperty("skills") @Schema(description = "List of mandatory skill the persons will be filtered on") @Size(min=1) List<String> skills,
        @JsonProperty(value = "includeInternalPersons", required = true) @NotNull @Schema(description = "Include persons who are NON_CROWD is mandatory") Boolean includeInternalPersons,
        @JsonProperty(value = "includeCrowdPersons", required = true) @NotNull @Schema(description = "Include persons who are CROWD is mandatory") Boolean includeCrowdPersons,
        @JsonProperty(value = "limit", required = true) @NotNull @Schema(description = "Limit the number of returned technicians, max 500") @Max(value = MAX_LIMIT, message = "Limit must be less than or equal to " + MAX_LIMIT) Integer limit) {

    public static final int MAX_LIMIT = 500;

}