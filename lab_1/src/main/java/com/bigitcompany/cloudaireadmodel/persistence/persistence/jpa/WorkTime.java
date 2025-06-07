package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "worktime")
public class WorkTime {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    private UUID person;

    private Instant startDateTime;

    private Instant endDateTime;

    @Column(name = "lastChanged", nullable = false)
    private Instant lastChanged;

    public WorkTime(Map<String, Object> workTimeRecord) {
        this.id = UuidMapper.toUUID((String) workTimeRecord.get("id"));
        this.person = UuidMapper.toUUID((String) workTimeRecord.get("person"));
        this.startDateTime = DateTimeService.toInstant((String) workTimeRecord.get("startDateTime"));
        this.endDateTime = DateTimeService.toInstant((String) workTimeRecord.get("endDateTime"));
        this.lastChanged = Instant.parse((String) workTimeRecord.get("lastChanged"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("person", UuidMapper.toFsmId(getPerson().toString()));
        map.put("startDateTime", Objects.toString(getStartDateTime(), null));
        map.put("endDateTime", Objects.toString(getEndDateTime(), null));
        map.put("lastChanged", getLastChanged());
        return map;
    }

    public WorkTime copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new WorkTime(merged);
    }

    public WorkTime() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getPerson() {
        return person;
    }

    public Instant getStartDateTime() {
        return startDateTime;
    }

    public Instant getEndDateTime() {
        return endDateTime;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }
}