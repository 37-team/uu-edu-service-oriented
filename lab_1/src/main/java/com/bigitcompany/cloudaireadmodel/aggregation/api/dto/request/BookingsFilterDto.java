package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public class BookingsFilterDto {

    @NotNull
    @JsonProperty("earliest")
    @Schema(type = "string", format = "date-time", example = "2019-08-01T14:00:00.000Z", required = true)
    private Instant earliest;

    @NotNull
    @JsonProperty("latest")
    @Schema(type = "string", format = "date-time", example = "2019-08-01T14:00:00.000Z", required = true)
    private Instant latest;

    @JsonProperty("activitiesToExclude")
    @Schema(
        type = "array",
        example = "[\"242999B5EA6A4ADFBE2DC38EB8A5AD5C\", \"242999b5-ea6a-4adf-be2d-c38eb8a5ad5c\"]",
        description = "Can be FSM IDs or UUIDs"
    )
    private List<String> activitiesToExclude;

    @JsonProperty("considerReleasedAsExclusive")
    @Schema(type = "boolean", example = "true", defaultValue = "true")
    private Boolean considerReleasedAsExclusive;

    @JsonProperty("considerPlannedAsExclusive")
    @Schema(type = "boolean", example = "false", defaultValue = "false")
    private Boolean considerPlannedAsExclusive;

    public BookingsFilterDto(Instant earliest,
                             Instant latest,
                             List<String> activitiesToExclude,
                             Boolean considerReleasedAsExclusive,
                             Boolean considerPlannedAsExclusive) {
        this.earliest = earliest;
        this.latest = latest;
        this.activitiesToExclude = activitiesToExclude;
        this.considerReleasedAsExclusive = considerReleasedAsExclusive;
        this.considerPlannedAsExclusive = considerPlannedAsExclusive;
    }

    public Instant getEarliest() {
        return earliest;
    }

    public Instant getLatest() {
        return latest;
    }

    public List<String> getActivitiesToExclude() {
        return activitiesToExclude;
    }

    public Boolean isConsiderReleasedAsExclusive() {
        return considerReleasedAsExclusive;
    }

    public Boolean isConsiderPlannedAsExclusive() {
        return considerPlannedAsExclusive;
    }
}
