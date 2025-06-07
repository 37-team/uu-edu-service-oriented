package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.data.event.common.ObjectRef;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "requirement")
public class Requirement {

    private static final String ACTIVITY_OBJECT_TYPE = "ACTIVITY";

    @Id
    private UUID id;

    private boolean mandatory;

    private UUID tag;

    private UUID activity;

    public Requirement() {
        // no-args constructor
    }

    public Requirement(Map<String, Object> map) {
        id = UuidMapper.toUUID((String) map.get("id"));

        if (map.get("object") != null) {
            var objectRef = (ObjectRef) map.get("object");
            if (objectRef.getObjectId() != null && ACTIVITY_OBJECT_TYPE.equalsIgnoreCase(objectRef.getObjectType())) {
                activity = UuidMapper.toUUID(objectRef.getObjectId());
            }
        }

        mandatory = (Boolean) map.get("mandatory");
        tag = UuidMapper.toUUID((String) map.get("tag"));
    }

    public UUID getId() {
        return id;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public UUID getTag() {
        return tag;
    }

    public UUID getActivity() {
        return activity;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("mandatory", isMandatory());
        map.put("tag", UuidMapper.toFsmId(getTag()));

        if (getActivity() != null) {
            var objectRefActivity = ObjectRef.newBuilder()
                .setObjectId(getActivity().toString())
                .setObjectType(ACTIVITY_OBJECT_TYPE)
                .build();
            map.put("activity", objectRefActivity);
        }

        return map;
    }

    public Requirement copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(toMap(), overrides);
        return new Requirement(merged);
    }


    public static final class Builder {

        private UUID id;

        private boolean mandatory;

        private UUID tag;

        private UUID activity;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder mandatory(boolean mandatory) {
            this.mandatory = mandatory;
            return this;
        }

        public Builder tag(UUID tag) {
            this.tag = tag;
            return this;
        }


        public Builder activity(UUID activity) {
            this.activity = activity;
            return this;
        }

        public Requirement build() {
            var requirement = new Requirement();
            requirement.mandatory = mandatory;
            requirement.id = id;
            requirement.tag = tag;
            requirement.activity = activity;
            return requirement;
        }
    }
}