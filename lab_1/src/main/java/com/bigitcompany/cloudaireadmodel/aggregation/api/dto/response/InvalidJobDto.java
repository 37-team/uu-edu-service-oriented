package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public class InvalidJobDto {

    @Schema(
        type = "string",
        format = "uuid",
        description = "Can be UUIDs"
    )
    private final String id;

    private final String reason;

    private final Instant lastChanged;

    public InvalidJobDto(String id, Instant lastChanged, String reason) {
        this.id = id;
        this.reason = reason;
        this.lastChanged = lastChanged;
    }

    public String getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }
}