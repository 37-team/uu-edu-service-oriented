package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Assignment {

    UUID resourceId;

    Instant startDateTime;

    Instant endDateTime;

    public Assignment(UUID resourceId, Instant startDateTime, Instant endDateTime) {
        this.resourceId = resourceId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public UUID getResourceId() {
        return resourceId;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(getResourceId(), that.getResourceId()) && Objects.equals(getStartDateTime(), that.getStartDateTime()) && Objects.equals(getEndDateTime(), that.getEndDateTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getResourceId(), getStartDateTime(), getEndDateTime());
    }

    @Override
    public String toString() {
        return "CurrentAssignmentStatus{" +
            "resourceId='" + resourceId + '\'' +
            ", startDateTime=" + startDateTime +
            ", endDateTime=" + endDateTime +
            '}';
    }
}