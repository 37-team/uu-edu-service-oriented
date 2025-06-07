package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.bigitcompany.cloudaireadmodel.persistence.events.mappers.UdfMapper;
import com.sap.fsm.data.event.common.Location;
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
@Entity(name = "person")
public final class Person {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "refid")
    private UUID refId;

    @Column(name = "locationlastuserchangeddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date locationLastUserChangedDate;

    @Type(JsonBinaryType.class)
    @Column(name = "location", columnDefinition = "jsonb")
    private Location location;

    @Column(name = "externalid")
    private String externalId;

    @Column(name = "crowdtype")
    private String crowdType;

    @Column(name = "type")
    private String type;

    @Column(name = "inactive")
    private Boolean inactive;

    @Column(name = "plannableresource")
    private Boolean plannableResource;

    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    @Type(JsonBinaryType.class)
    @Column(name = "udfValues", columnDefinition = "jsonb")
    private List<UdfValue> udfValues;

    public static final String ID_KEY = "id";

    public static final String REF_ID_KEY = "refId";

    public static final String EXTERNAL_ID_KEY = "externalId";

    public static final String TYPE_KEY = "type";

    public static final String CROWD_TYPE_KEY = "crowdType";

    public static final String INACTIVE_KEY = "inactive";

    public static final String PLANNABLE_RESOURCE_KEY = "plannableResource";

    public static final String LAST_CHANGED_KEY = "lastChanged";

    public static final String LOCATION_LAST_USER_CHANGED_DATE_KEY = "locationLastUserChangedDate";

    public static final String LOCATION_KEY = "location";

    public static final String UDF_VALUES_KEY = "udfValues";

    public Person() {

    }

    public Person(Map<String, Object> map) {
        this.id = this.getUuidFromString((String) map.get(ID_KEY));
        this.refId = this.getUuidFromString((String) map.get(REF_ID_KEY));
        this.type = ((String) map.get(TYPE_KEY));
        this.inactive = ((Boolean) map.get(INACTIVE_KEY));
        this.plannableResource = ((Boolean) map.get(PLANNABLE_RESOURCE_KEY));
        this.crowdType = ((String) map.get(CROWD_TYPE_KEY));
        this.lastChanged = DateTimeService.toDate((String) map.get(LAST_CHANGED_KEY));
        this.externalId = ((String) map.get(EXTERNAL_ID_KEY));
        this.udfValues = UdfMapper.toUdfValueList((GenericData.Array<UdfValue>) map.get(UDF_VALUES_KEY));
        this.locationLastUserChangedDate = DateTimeService.toDate((String) map.get(LOCATION_LAST_USER_CHANGED_DATE_KEY));
        this.location = (Location) map.get(LOCATION_KEY);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ID_KEY, UuidMapper.toFsmId(getId().toString()));
        map.put(REF_ID_KEY, UuidMapper.toFsmId(getRefId().toString()));
        map.put(TYPE_KEY, getType());
        map.put(INACTIVE_KEY, getInactive());
        map.put(PLANNABLE_RESOURCE_KEY, getPlannableResource());
        map.put(CROWD_TYPE_KEY, getCrowdType());
        map.put(LAST_CHANGED_KEY, DateTimeService.instantToString(getLastChanged()));
        map.put(EXTERNAL_ID_KEY, getExternalId());
        map.put(UDF_VALUES_KEY, UdfMapper.toGenericDataArray(getUdfValues()));
        map.put(LOCATION_LAST_USER_CHANGED_DATE_KEY, DateTimeService.instantToString(getLocationLastUserChangedDate()));
        map.put(LOCATION_KEY, getLocation());
        return map;
    }

    public Person copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new Person(merged);
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

    public UUID getRefId() {
        return refId;
    }


    public Instant getLocationLastUserChangedDate() {
        if (locationLastUserChangedDate == null) {
            return null;
        }
        return locationLastUserChangedDate.toInstant();
    }

    public Location getLocation() {
        return location;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getCrowdType() {
        return crowdType;
    }

    public String getType() {
        return type;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public Boolean getPlannableResource() {
        return plannableResource;
    }

    public Instant getLastChanged() {
        if (lastChanged == null) {
            return null;
        }
        return lastChanged.toInstant();
    }

    public List<UdfValue> getUdfValues() {
        return udfValues;
    }
}