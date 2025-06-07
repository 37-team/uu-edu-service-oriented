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
import java.util.Objects;
import java.util.UUID;


@Valid
@Entity(name = "businesspartner")
public final class BusinessPartner {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "externalId", unique = true)
    private String externalId;

    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    @Type(JsonBinaryType.class)
    @Column(name = "udfValues", columnDefinition = "jsonb")
    private List<UdfValue> udfValues;

    public BusinessPartner() {

    }

    public BusinessPartner(Map<String, Object> map) {
        this.id = UuidMapper.toUUID((String) map.get("id"));
        this.externalId = (String) map.get("externalId");
        this.lastChanged = DateTimeService.toDate((String) map.get("lastChanged"));
        this.udfValues = UdfMapper.toUdfValueList((GenericData.Array<UdfValue>) map.get("udfValues"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("externalId", Objects.toString(getExternalId(), null));
        map.put("lastChanged", getLastChanged());
        map.put("udfValues", UdfMapper.toGenericDataArray(getUdfValues()));
        return map;
    }

    public BusinessPartner copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new BusinessPartner(merged);
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Instant getLastChanged() {
        return lastChanged.toInstant();
    }

    public List<UdfValue> getUdfValues() {
        return udfValues;
    }
}