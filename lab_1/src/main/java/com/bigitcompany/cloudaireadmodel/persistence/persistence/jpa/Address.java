package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.data.event.common.ObjectRef;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.AccessType;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "address")
@AccessType(AccessType.Type.FIELD)
public final class Address {

    /*
     * Attention this enum is not a complete representation of all possible ObjectTypes
     */
    public enum ObjectType {
        PERSON,
        ACTIVITY,
        BUSINESSPARTNER,
        UNIFIEDPERSON
    }

    public static final String ID_FIELD = "id";

    public static final String LOCATION_FIELD = "location";

    public static final String OBJECT_FIELD = "object";

    public static final String LAST_CHANGED_FIELD = "lastChanged";

    public static final String TYPE_FIELD = "type";

    public static final String DEFAULT_ADDRESS_FIELD = "defaultAddress";

    @Id
    private UUID id;

    private UUID objectId;

    private String objectType;

    private String type;

    private boolean defaultAddress;

    @Type(JsonBinaryType.class)
    private Location location;

    @NotNull
    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    public Address() {
    }

    public Address(UUID id, Location location) {
        this.id = id;
        this.location = location;
    }

    public Address(Map<String, Object> address) {
        id = UuidMapper.toUUID((String) address.get(ID_FIELD));
        lastChanged = DateTimeService.toDate((String) address.get(LAST_CHANGED_FIELD));

        if (address.get(OBJECT_FIELD) != null) {
            var objectRef = (ObjectRef) address.get(OBJECT_FIELD);
            if (objectRef.getObjectId() != null) {
                objectId = UuidMapper.toUUID(objectRef.getObjectId());
                objectType = objectRef.getObjectType();
            }
        }

        if (address.containsKey(LOCATION_FIELD) && address.get(LOCATION_FIELD) != null) {
            var eventLocation = (com.sap.fsm.data.event.common.Location) address.get(LOCATION_FIELD);
            location = new Location(eventLocation);
        }

        if (address.containsKey(DEFAULT_ADDRESS_FIELD) && address.get(DEFAULT_ADDRESS_FIELD) != null) {
            defaultAddress = (Boolean) address.get(DEFAULT_ADDRESS_FIELD);
        }

        type = (String) address.get(TYPE_FIELD);
    }

    public Location getLocation() {
        return location;
    }

    public UUID getId() {
        return id;
    }

    public UUID getObjectId() {
        return objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public Instant getLastChanged() {
        return lastChanged == null ? null : lastChanged.toInstant();
    }

    public String getType() {
        return type;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public Address copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(toMap(), overrides);
        return new Address(merged);
    }

    private Map<String, Object> toMap() {
        Map<String, Object> fullAttributes = new HashMap<>();
        fullAttributes.put(ID_FIELD, UuidMapper.toFsmId(getId().toString()));
        if (getLocation() != null) {
            fullAttributes.put(LOCATION_FIELD, toFsmEventLocation(getLocation()));
        }
        if (getObjectId() != null && getObjectType() != null) {
            fullAttributes.put(OBJECT_FIELD, new ObjectRef(getObjectId().toString(), getObjectType()));
        }
        fullAttributes.put(LAST_CHANGED_FIELD, Objects.toString(getLastChanged(), null));
        fullAttributes.put(TYPE_FIELD, Objects.toString(getType(), null));
        fullAttributes.put(DEFAULT_ADDRESS_FIELD, isDefaultAddress());
        return fullAttributes;
    }

    private Object toFsmEventLocation(Location location) {
        return new com.sap.fsm.data.event.common.Location(location.getLatitude(), location.getLongitude());
    }
}
