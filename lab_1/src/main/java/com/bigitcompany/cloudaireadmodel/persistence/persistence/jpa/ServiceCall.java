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
import org.apache.avro.generic.GenericData;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Valid
@Entity(name = "serviceCall")
public final class ServiceCall {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "externalId", unique = true)
    private String externalId;

    @Column(name = "priority")
    private String priority;

    @Column(name = "businessPartner")
    private UUID businessPartner;

    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    @Type(JsonBinaryType.class)
    @Column(name = "udfValues", columnDefinition = "jsonb")
    private List<UdfValue> udfValues;

    public ServiceCall() {

    }

    public ServiceCall(Map<String, Object> map) {
        this.id = this.getUuidFromString((String) map.get("id"));
        this.externalId = (String) map.get("externalId");
        this.priority = (String) map.get("priority");
        this.businessPartner = this.getUuidFromString((String) map.get("businessPartner"));
        this.lastChanged = DateTimeService.toDate((String) map.get("lastChanged"));
        this.udfValues = UdfMapper.toUdfValueList((GenericData.Array<UdfValue>) map.get("udfValues"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("externalId", getExternalId());
        map.put("priority", getPriority());
        map.put("businessPartner", UuidMapper.toFsmId(getBusinessPartner().toString()));
        map.put("lastChanged", getLastChanged());
        map.put("udfValues", UdfMapper.toGenericDataArray(getUdfValues()));
        return map;
    }

    public ServiceCall copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new ServiceCall(merged);
    }

    private UUID getUuidFromString(String id) {
        if (id == null) {
            return null;
        }
        return UuidMapper.toUUID(id);
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getPriority() {
        return priority;
    }

    public UUID getBusinessPartner() {
        return businessPartner;
    }

    public Instant getLastChanged() {
        return lastChanged.toInstant();
    }

    public List<UdfValue> getUdfValues() {
        return udfValues;
    }
}