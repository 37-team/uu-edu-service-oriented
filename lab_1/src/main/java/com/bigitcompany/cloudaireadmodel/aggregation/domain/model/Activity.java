package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Activity extends Entity {

    private final Instant lastChanged;

    private final Instant earliestStartDateTime;

    private final Instant dueDateTime;

    private final Integer travelTimeToInMinutes;

    private final Integer travelTimeFromInMinutes;

    private final ExecutionStage executionStage;

    private final Integer durationMinutes;

    private final Integer plannedDurationInMinutes;

    private final String syncStatus;

    private final List<UUID> responsibles;

    private final Instant startDateTime;

    private final Instant endDateTime;

    @SuppressWarnings("java:S107")
    public Activity(UUID id, String externalId, Map<String, String> udfValues, Instant lastChanged, Instant earliestStartDateTime, Instant dueDateTime, Integer travelTimeToInMinutes, Integer travelTimeFromInMinutes, ExecutionStage executionStage, Integer durationMinutes, String syncStatus, List<UUID> responsibles, Instant startDateTime, Instant endDateTime, Integer plannedDurationInMinutes) {
        super(id, externalId, udfValues);
        this.lastChanged = lastChanged;
        this.earliestStartDateTime = earliestStartDateTime;
        this.dueDateTime = dueDateTime;
        this.travelTimeToInMinutes = travelTimeToInMinutes;
        this.travelTimeFromInMinutes = travelTimeFromInMinutes;
        this.executionStage = executionStage;
        this.durationMinutes = durationMinutes;
        this.plannedDurationInMinutes = plannedDurationInMinutes;
        this.syncStatus = syncStatus;
        this.responsibles = responsibles;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Instant getEarliestStartDateTime() {
        return earliestStartDateTime;
    }

    public Instant getDueDateTime() {
        return dueDateTime;
    }

    public Integer getTravelTimeToInMinutes() {
        return travelTimeToInMinutes;
    }

    public Integer getTravelTimeFromInMinutes() {
        return travelTimeFromInMinutes;
    }

    public ExecutionStage getExecutionStage() {
        return executionStage;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Integer getPlannedDurationInMinutes() {
        return plannedDurationInMinutes;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    public List<UUID> getResponsibles() {
        return responsibles;
    }

    public UUID getResourceId() {
        return responsibles != null && !responsibles.isEmpty() ? responsibles.get(0) : null;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Activity activity = (Activity) o;
        return Objects.equals(lastChanged, activity.lastChanged) && Objects.equals(earliestStartDateTime, activity.earliestStartDateTime) && Objects.equals(dueDateTime, activity.dueDateTime) && Objects.equals(travelTimeToInMinutes, activity.travelTimeToInMinutes) && Objects.equals(travelTimeFromInMinutes, activity.travelTimeFromInMinutes) && executionStage == activity.executionStage && Objects.equals(durationMinutes, activity.durationMinutes) && Objects.equals(syncStatus, activity.syncStatus) && Objects.equals(responsibles, activity.responsibles) && Objects.equals(startDateTime, activity.startDateTime) && Objects.equals(endDateTime, activity.endDateTime);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lastChanged, earliestStartDateTime, dueDateTime, travelTimeToInMinutes, travelTimeFromInMinutes, executionStage, durationMinutes, syncStatus, responsibles, startDateTime, endDateTime);
    }

    @Generated
    @Override
    public String toString() {
        return "Activity{" +
                "lastChanged=" + lastChanged +
                ", earliestStartDateTime=" + earliestStartDateTime +
                ", dueDateTime=" + dueDateTime +
                ", travelTimeToInMinutes=" + travelTimeToInMinutes +
                ", travelTimeFromInMinutes=" + travelTimeFromInMinutes +
                ", executionStage=" + executionStage +
                ", durationMinutes=" + durationMinutes +
                ", syncStatus='" + syncStatus + '\'' +
                ", responsibles=" + responsibles +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }
}
