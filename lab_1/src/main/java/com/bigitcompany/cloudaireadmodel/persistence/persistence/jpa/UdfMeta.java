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
@Table(name = "udfMeta")
public class UdfMeta {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "lastChanged")
    private Instant lastChanged;

    @Column(name = "lastIndexed")
    private Instant lastIndexed;


    public UdfMeta() {
        // no-args constructor
    }

    public UdfMeta(Map<String, Object> udfMetaRecord) {
        this.id = UuidMapper.toUUID((String) udfMetaRecord.get("id"));
        this.name = (String) udfMetaRecord.get("name");
        this.lastChanged = Instant.parse((String) udfMetaRecord.get("lastChanged"));
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Instant getLastIndexed() {
        return lastIndexed;
    }


    public UdfMeta copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new UdfMeta(merged);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", UuidMapper.toFsmId(this.getId()));
        values.put("name", this.getName());
        values.put("lastChanged", this.getLastChanged().toString());
        return values;
    }
}