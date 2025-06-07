package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class BookingJob {

    private final UUID id;

    private final UUID serviceCallId;

    private final UUID equipmentId;

    private final UUID businessPartnerId;

    private final Instant lastChanged;

    private final Location location;

    private Entity equipment;

    private Entity businessPartner;

    // Merged udf values from activity and serviceCall
    private final Map<String, String> udfValues;

    public BookingJob(UUID id, UUID serviceCallId, Instant lastChanged, Location location, UUID equipmentId, UUID businessPartnerId) {
        this.id = id;
        this.serviceCallId = serviceCallId;
        this.lastChanged = lastChanged;
        this.location = location;
        udfValues = new HashMap<>();
        this.equipmentId = equipmentId;
        this.businessPartnerId = businessPartnerId;

    }

    public UUID getId() {
        return id;
    }

    public UUID getServiceCallId() {
        return serviceCallId;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getEquipmentId() {
        return equipment == null ? equipmentId : equipment.getId();
    }

    public Entity getEquipment() {
        return equipment;
    }

    public UUID getBusinessPartnerId() {
        return businessPartner == null ? businessPartnerId : businessPartner.getId();
    }
    public Entity getBusinessPartner() {
        return businessPartner;
    }


    public Map<String, String> getUdfValues() {
        return Collections.unmodifiableMap(udfValues);
    }

    // mutators
    public void addUdf(String key, String value) {
        if (key != null && value != null) {
            udfValues.put(key, value);
        }
    }

    public void addUdfValues(Map<String, String> udfValues) {
        if (udfValues != null && !udfValues.isEmpty()) {
            this.udfValues.putAll(udfValues);
        }
    }

    public void setEquipment(Entity equipment) {
        this.equipment = equipment;
    }

    public void setBusinessPartner(Entity businessPartner) {
        this.businessPartner = businessPartner;
    }

    @Override
    public String toString() {
        return "BookingJob{" +
            "id='" + id + '\'' +
            ", lastChanged=" + lastChanged +
            ", location=" + location +
            ", equipment=" + equipment +
            ", businessPartner=" + businessPartner +
            ", udfValues=" + udfValues +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingJob that = (BookingJob) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getLastChanged(), that.getLastChanged()) && Objects.equals(getLocation(), that.getLocation()) && Objects.equals(getEquipment(), that.getEquipment()) && Objects.equals(getUdfValues(), that.getUdfValues()) && Objects.equals(getBusinessPartner(), that.getBusinessPartner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLastChanged(), getLocation(), getEquipment(), getUdfValues(), getBusinessPartner());
    }
}
