package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.bigitcompany.cloudaireadmodel.persistence.events.mappers.UdfMapper;
import com.sap.fsm.data.event.common.UdfValue;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Valid
@Entity(name = "equipment")
public final class Equipment {

    public static final String TABLE_NAME = "equipment";

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "externalId", unique = true)
    private String externalId;

    @Type(JsonBinaryType.class)
    @Column(name = "udfValues", columnDefinition = "jsonb")
    private List<UdfValue> udfValues;

    @NotNull
    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    public Equipment() {

    }

    public Equipment(Map<String, Object> map) {
        this.id = UuidMapper.toUUID((String) map.get("id"));
        this.externalId = (String) map.get("externalId");
        this.udfValues = UdfMapper.toUdfValueList((GenericData.Array<UdfValue>) map.get("udfValues"));
        this.lastChanged = DateTimeService.toDate((String) map.get("lastChanged"));
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getLastChanged() {
        return lastChanged == null ? null : lastChanged.toInstant();
    }

    public List<UdfValue> getUdfValues() {
        return udfValues;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("externalId", Objects.toString(getExternalId(), null));
        map.put("udfValues", UdfMapper.toGenericDataArray(getUdfValues()));
        map.put("lastChanged", Objects.toString(getLastChanged(), null));
        return map;
    }

    public Equipment copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new Equipment(merged);
    }

    @Override
    public String toString() {
        return "Equipment{" +
            "id=" + id +
            ", externalId=" + externalId +
            ", udfValues=" + udfValues +
            ", lastChanged=" + lastChanged +
            '}';
    }

    public static final class Builder {
        private UUID id;

        private String externalId;

        private List<UdfValue> udfValues;

        private Date lastChanged;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder udfValues(List<UdfValue> udfValues) {
            this.udfValues = udfValues;
            return this;
        }

        public Builder lastChanged(Date lastChanged) {
            this.lastChanged = lastChanged;
            return this;
        }

        public Equipment build() {
            var equipment = new Equipment();
            equipment.lastChanged = this.lastChanged;
            equipment.id = this.id;
            equipment.externalId = this.externalId;
            equipment.udfValues = this.udfValues;
            return equipment;
        }
    }
}