package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.time.Instant;

public class ActivityDto {

    private String id;

    private String externalId;

    private Instant lastChanged;

    private Instant earliestStartDateTime;

    private Integer travelTimeToInMinutes;

    private Integer travelTimeFromInMinutes;

    private Integer plannedDurationInMinutes;

    public ActivityDto(String id,
                       String externalId,
                       Instant lastChanged,
                       Instant earliestStartDateTime,
                       Integer travelTimeToInMinutes,
                       Integer travelTimeFromInMinutes,
                       Integer plannedDurationInMinutes
    ) {
        this.id = id;
        this.externalId = externalId;
        this.lastChanged = lastChanged;
        this.earliestStartDateTime = earliestStartDateTime;
        this.travelTimeToInMinutes = travelTimeToInMinutes;
        this.travelTimeFromInMinutes = travelTimeFromInMinutes;
        this.plannedDurationInMinutes = plannedDurationInMinutes;
    }

    public ActivityDto() {
    }

    public String getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Instant getEarliestStartDateTime() {
        return earliestStartDateTime;
    }

    public Integer getTravelTimeToInMinutes() {
        return travelTimeToInMinutes;
    }

    public Integer getTravelTimeFromInMinutes() {
        return travelTimeFromInMinutes;
    }

    public Integer getPlannedDurationInMinutes() {
        return plannedDurationInMinutes;
    }
}
