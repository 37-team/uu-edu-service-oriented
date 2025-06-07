package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.time.Instant;

public class AssignmentDto {

    String resourceId;

    Instant startDateTime;

    Instant endDateTime;

    public AssignmentDto(String resourceId, Instant startDateTime, Instant endDateTime) {
        this.resourceId = resourceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public AssignmentDto() {
    }

    public String getResourceId() {
        return resourceId;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }
}