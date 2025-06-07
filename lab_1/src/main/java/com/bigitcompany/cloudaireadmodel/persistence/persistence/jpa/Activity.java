package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.bigitcompany.cloudaireadmodel.persistence.events.mappers.UdfMapper;
import com.sap.fsm.data.event.common.ObjectRef;
import com.sap.fsm.data.event.common.UdfValue;
import io.hypersistence.utils.hibernate.type.array.UUIDArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.avro.generic.GenericData;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@Valid
@Entity(name = "activity")
public final class Activity {

    private static final String OBJECT_REF_PROPERTY = "object";

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "externalId", unique = true)
    private String externalId;

    @Column(name = "address")
    private UUID address;

    @Column(name = "serviceCall")
    private UUID serviceCall;

    @Column(name = "equipment")
    private UUID equipment;

    @Column(name = "businessPartner")
    private UUID businessPartner;

    @NotNull
    @Column(name = "responsibles", columnDefinition = "uuid[]", nullable = false)
    @Type(UUIDArrayType.class)
    private UUID[] responsibles;

    @NotNull
    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    @Column(name = "earliestStartDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date earliestStartDateTime;

    @Column(name = "dueDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dueDateTime;

    @Column(name = "startDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateTime;

    @Column(name = "endDateTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateTime;

    @Column(name = "executionStage")
    private String executionStage;

    @Column(name = "syncStatus")
    private String syncStatus;

    @Column(name = "durationInMinutes")
    private Integer durationInMinutes;

    @Column(name = "plannedDurationInMinutes")
    private Integer plannedDurationInMinutes;

    @Column(name = "travel_time_to_in_minutes")
    private Integer travelTimeToInMinutes;

    @Column(name = "travel_time_from_in_minutes")
    private Integer travelTimeFromInMinutes;

    @Type(JsonBinaryType.class)
    @Column(name = "udfValues", columnDefinition = "jsonb")
    private List<UdfValue> udfValues;

    public Activity() {

    }

    public Activity(Map<String, Object> map) {
        this.id = UuidMapper.toUUID((String) map.get("id"));
        this.externalId = (String) map.get("externalId");
        this.address = UuidMapper.toUUID((String) map.get("address"));

        if (map.get(OBJECT_REF_PROPERTY) != null) {
            var objectRef = (ObjectRef) map.get(OBJECT_REF_PROPERTY);
            if (objectRef.getObjectId() != null) {
                this.serviceCall = UuidMapper.toUUID(objectRef.getObjectId());
            }
        }
        this.equipment = UuidMapper.toUUID((String) map.get("equipment"));
        this.businessPartner = UuidMapper.toUUID((String) map.get("businessPartner"));
        this.responsibles = UuidMapper.toUUIDs((GenericData.Array<String>) map.get("responsibles"));
        this.udfValues = UdfMapper.toUdfValueList((GenericData.Array<UdfValue>) map.get("udfValues"));

        // Instant
        this.lastChanged = DateTimeService.toDate((String) map.get("lastChanged"));
        this.earliestStartDateTime = DateTimeService.toDate((String) map.get("earliestStartDateTime"));
        this.dueDateTime = DateTimeService.toDate((String) map.get("dueDateTime"));
        this.startDateTime = DateTimeService.toDate((String) map.get("startDateTime"));
        this.endDateTime = DateTimeService.toDate((String) map.get("endDateTime"));

        // Values
        this.executionStage = (String) map.get("executionStage");
        this.syncStatus = (String) map.get("syncStatus");
        this.durationInMinutes = (Integer) map.get("durationInMinutes");
        this.plannedDurationInMinutes = (Integer) map.get("plannedDurationInMinutes");
        this.travelTimeToInMinutes = (Integer) map.get("travelTimeToInMinutes");
        this.travelTimeFromInMinutes = (Integer) map.get("travelTimeFromInMinutes");
    }


    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public UUID getAddress() {
        return address;
    }

    public UUID getServiceCall() {
        return serviceCall;
    }

    public UUID getEquipment() {
        return equipment;
    }

    public UUID getBusinessPartner() {
        return businessPartner;
    }

    public List<UUID> getResponsibles() {
        return List.of(responsibles);
    }

    public Instant getLastChanged() {
        return lastChanged == null ? null : lastChanged.toInstant();
    }

    public Instant getEarliestStartDateTime() {
        return earliestStartDateTime == null ? null : earliestStartDateTime.toInstant();
    }

    public Instant getDueDateTime() {
        return dueDateTime == null ? null : dueDateTime.toInstant();
    }

    public Instant getStartDateTime() {
        return startDateTime == null ? null : startDateTime.toInstant();
    }

    public Instant getEndDateTime() {
        return endDateTime == null ? null : endDateTime.toInstant();
    }

    public String getExecutionStage() {
        return executionStage;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public Integer getDurationInMinutes() {
        return durationInMinutes;
    }

    public Integer getPlannedDurationInMinutes() {
        return plannedDurationInMinutes;
    }

    public Integer getTravelTimeToInMinutes() {
        return travelTimeToInMinutes;
    }

    public Integer getTravelTimeFromInMinutes() {
        return travelTimeFromInMinutes;
    }

    public List<UdfValue> getUdfValues() {
        return udfValues;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("externalId", Objects.toString(getExternalId(), null));
        map.put("address", Objects.toString(getAddress(), null));
        if (getServiceCall() != null) {
            map.put(OBJECT_REF_PROPERTY, new ObjectRef(getServiceCall().toString(), "SERVICECALL"));
        }
        map.put("equipment", Objects.toString(getEquipment(), null));
        map.put("businessPartner", Objects.toString(getBusinessPartner(), null));
        map.put("responsibles", UuidMapper.toGenericDataArray(getResponsibles()));
        map.put("lastChanged", Objects.toString(getLastChanged(), null));
        map.put("earliestStartDateTime", Objects.toString(getEarliestStartDateTime(), null));
        map.put("dueDateTime", Objects.toString(getDueDateTime(), null));
        map.put("startDateTime", Objects.toString(getStartDateTime(), null));
        map.put("endDateTime", Objects.toString(getEndDateTime(), null));
        map.put("executionStage", getExecutionStage());
        map.put("syncStatus", getSyncStatus());
        map.put("durationInMinutes", getDurationInMinutes());
        map.put("plannedDurationInMinutes", getPlannedDurationInMinutes());
        map.put("travelTimeToInMinutes", getTravelTimeToInMinutes());
        map.put("travelTimeFromInMinutes", getTravelTimeFromInMinutes());
        map.put("udfValues", UdfMapper.toGenericDataArray(getUdfValues()));
        return map;
    }

    public Activity copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new Activity(merged);
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + id +
            ", externalId=" + externalId +
            ", address=" + address +
            ", serviceCall=" + serviceCall +
            ", equipment=" + equipment +
            ", businessPartner=" + businessPartner +
            ", responsibles=" + Arrays.toString(responsibles) +
            ", lastChanged=" + lastChanged +
            ", earliestStartDateTime=" + earliestStartDateTime +
            ", dueDateTime=" + dueDateTime +
            ", startDateTime=" + startDateTime +
            ", endDateTime=" + endDateTime +
            ", executionStage='" + executionStage + '\'' +
            ", syncStatus='" + syncStatus + '\'' +
            ", durationInMinutes=" + durationInMinutes +
            ", plannedDurationInMinutes=" + plannedDurationInMinutes +
            ", travelTimeToInMinutes=" + travelTimeToInMinutes +
            ", travelTimeFromInMinutes=" + travelTimeFromInMinutes +
            ", udfValues=" + udfValues +
            '}';
    }

    public static final class Builder {
        private UUID id;

        private String externalId;

        private UUID address;

        private UUID serviceCall;

        private UUID equipment;

        private UUID businessPartner;

        private UUID[] responsibles;

        private Date lastChanged;

        private Date earliestStartDateTime;

        private Date dueDateTime;

        private Date startDateTime;

        private Date endDateTime;

        private String executionStage;

        private String syncStatus;

        private Integer durationInMinutes;

        private Integer plannedDurationInMinutes;

        private Integer travelTimeToInMinutes;

        private Integer travelTimeFromInMinutes;

        private List<UdfValue> udfValues;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder address(UUID address) {
            this.address = address;
            return this;
        }

        public Builder serviceCall(UUID serviceCall) {
            this.serviceCall = serviceCall;
            return this;
        }

        public Builder equipment(UUID equipment) {
            this.equipment = equipment;
            return this;
        }

        public Builder businessPartner(UUID businessPartner) {
            this.businessPartner = businessPartner;
            return this;
        }

        public Builder responsibles(UUID[] responsibles) {
            this.responsibles = responsibles;
            return this;
        }

        public Builder lastChanged(Date lastChanged) {
            this.lastChanged = lastChanged;
            return this;
        }

        public Builder earliestStartDateTime(Date earliestStartDateTime) {
            this.earliestStartDateTime = earliestStartDateTime;
            return this;
        }

        public Builder dueDateTime(Date dueDateTime) {
            this.dueDateTime = dueDateTime;
            return this;
        }

        public Builder startDateTime(Date startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }

        public Builder endDateTime(Date endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }

        public Builder executionStage(String executionStage) {
            this.executionStage = executionStage;
            return this;
        }

        public Builder syncStatus(String syncStatus) {
            this.syncStatus = syncStatus;
            return this;
        }

        public Builder durationInMinutes(Integer durationInMinutes) {
            this.durationInMinutes = durationInMinutes;
            return this;
        }

        public Builder plannedDurationInMinutes(Integer plannedDurationInMinutes) {
            this.plannedDurationInMinutes = plannedDurationInMinutes;
            return this;
        }

        public Builder travelTimeToInMinutes(Integer travelTimeToInMinutes) {
            this.travelTimeToInMinutes = travelTimeToInMinutes;
            return this;
        }

        public Builder travelTimeFromInMinutes(Integer travelTimeFromInMinutes) {
            this.travelTimeFromInMinutes = travelTimeFromInMinutes;
            return this;
        }

        public Builder udfValues(List<UdfValue> udfValues) {
            this.udfValues = udfValues;
            return this;
        }

        public Activity build() {
            var activity = new Activity();
            activity.lastChanged = this.lastChanged;
            activity.startDateTime = this.startDateTime;
            activity.executionStage = this.executionStage;
            activity.responsibles = this.responsibles;
            activity.plannedDurationInMinutes = this.plannedDurationInMinutes;
            activity.endDateTime = this.endDateTime;
            activity.id = this.id;
            activity.externalId = this.externalId;
            activity.dueDateTime = this.dueDateTime;
            activity.syncStatus = this.syncStatus;
            activity.address = this.address;
            activity.serviceCall = this.serviceCall;
            activity.earliestStartDateTime = this.earliestStartDateTime;
            activity.durationInMinutes = this.durationInMinutes;
            activity.equipment = this.equipment;
            activity.businessPartner = this.businessPartner;
            activity.travelTimeToInMinutes = this.travelTimeToInMinutes;
            activity.travelTimeFromInMinutes = this.travelTimeFromInMinutes;
            activity.udfValues = this.udfValues;
            return activity;
        }
    }
}