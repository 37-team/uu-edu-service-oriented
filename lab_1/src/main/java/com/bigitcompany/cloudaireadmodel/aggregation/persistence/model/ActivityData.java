package com.bigitcompany.cloudaireadmodel.aggregation.persistence.model;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ExecutionStage;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ActivityData {
    private UUID id;

    private String externalId;

    private UUID serviceCallId;

    private String serviceCallExternalId;

    private UUID equipmentId;

    private String equipmentExternalId;

    private UUID businessPartnerId;

    private String businessPartnerExternalId;

    private Instant earliestStart;

    private Instant due;

    private Location location;

    private Integer durationMinutes;

    private Integer plannedDurationInMinutes;

    private ExecutionStage executionStage;

    private String syncStatus;

    private List<UUID> responsibles;

    private Instant start;

    private Instant end;

    private Instant lastChanged;

    private Integer travelTimeToInMinutes;

    private Integer travelTimeFromInMinutes;

    private String priority;

    public ActivityData(UUID id, String externalId, UUID serviceCallId, String serviceCallExternalId, UUID equipmentId, String equipmentExternalId, UUID businessPartnerId, String businessPartnerExternalId, Instant earliestStart, Instant due, Location location, Integer durationMinutes, ExecutionStage executionStage, String syncStatus, List<UUID> responsibles, Instant start, Instant end, Instant lastChanged, Integer travelTimeToInMinutes, Integer travelTimeFromInMinutes, String priority, Integer plannedDurationInMinutes) {
        this.id = id;
        this.externalId = externalId;
        this.serviceCallId = serviceCallId;
        this.serviceCallExternalId = serviceCallExternalId;
        this.equipmentId = equipmentId;
        this.equipmentExternalId = equipmentExternalId;
        this.businessPartnerId = businessPartnerId;
        this.businessPartnerExternalId = businessPartnerExternalId;
        this.earliestStart = earliestStart;
        this.due = due;
        this.location = location;
        this.durationMinutes = durationMinutes;
        this.executionStage = executionStage;
        this.syncStatus = syncStatus;
        this.responsibles = responsibles;
        this.start = start;
        this.end = end;
        this.lastChanged = lastChanged;
        this.travelTimeToInMinutes = travelTimeToInMinutes;
        this.travelTimeFromInMinutes = travelTimeFromInMinutes;
        this.priority = priority;
        this.plannedDurationInMinutes = plannedDurationInMinutes;
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public UUID getServiceCallId() {
        return serviceCallId;
    }

    public String getServiceCallExternalId() {
        return serviceCallExternalId;
    }
    public UUID getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentExternalId() {
        return equipmentExternalId;
    }
    public UUID getBusinessPartnerId() {
        return businessPartnerId;
    }

    public String getBusinessPartnerExternalId() {
        return businessPartnerExternalId;
    }

    public Instant getEarliestStart() {
        return earliestStart;
    }

    public Instant getDue() {
        return due;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Integer getPlannedDurationInMinutes() {
        return plannedDurationInMinutes;
    }

    public ExecutionStage getExecutionStage() {
        return executionStage;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public List<UUID> getResponsibles() {
        return responsibles;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Integer getTravelTimeToInMinutes() {
        return travelTimeToInMinutes;
    }

    public Integer getTravelTimeFromInMinutes() {
        return travelTimeFromInMinutes;
    }

    public String getPriority() {
        return priority;
    }
}
