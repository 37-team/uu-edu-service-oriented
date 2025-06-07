package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.time.Instant;
import java.util.UUID;

public class InvalidJob {

    private final UUID id;

    private final String reason;

    private final Instant lastChanged;

    public InvalidJob(UUID id, Instant lastChanged, String reason) {
        this.id = id;
        this.reason = reason;
        this.lastChanged = lastChanged;
    }

    public UUID getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }
}