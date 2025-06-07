package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastChanged")
    private Instant lastChanged;

    public Tag() {
        // no-args constructor
    }

    public Tag(Map<String, Object> tagRecord) {

        this.id = UuidMapper.toUUID((String) tagRecord.get("id"));
        this.name = (String) tagRecord.get("name");
        this.lastChanged = Instant.parse((String) tagRecord.get("lastChanged"));
    }


    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Tag copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new Tag(merged);
    }

    public Map<String, Object> toMap() {

        Map<String, Object> values = new HashMap<>();
        values.put("id", UuidMapper.toFsmId(getId()));
        values.put("name", getName());
        values.put("lastChanged", getLastChanged().toString());
        return values;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public static final class Builder {
        private UUID id;

        private String name;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Tag build() {
            var tag = new Tag();
            tag.name = this.name;
            tag.id = this.id;
            return tag;
        }
    }
}
