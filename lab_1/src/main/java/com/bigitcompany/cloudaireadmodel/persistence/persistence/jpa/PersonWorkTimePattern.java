package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Valid
@Entity(name = "personworktimepattern")
public final class PersonWorkTimePattern {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "person")
    private UUID person;

    @Column(name = "startdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "enddate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "worktimepattern")
    private UUID workTimePattern;

    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    public PersonWorkTimePattern() {
    }

    public PersonWorkTimePattern(Map<String, Object> map) {
        this.id = UuidMapper.toUUID((String) map.get("id"));
        this.person = UuidMapper.toUUID((String) map.get("person"));
        this.startDate = this.getLocalDateFromString((String) map.get("startDate"));
        this.endDate = this.getLocalDateFromString((String) map.get("endDate"));
        this.workTimePattern = UuidMapper.toUUID((String) map.get("workTimePattern"));
        this.lastChanged = DateTimeService.toDate((String) map.get("lastChanged"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("person", Objects.toString(getPerson(), null));
        map.put("startDate", this.getLocalDateStringFromDate(getStartDate()));
        map.put("endDate", this.getLocalDateStringFromDate(getEndDate()));
        map.put("workTimePattern", Objects.toString(getWorkTimePattern(), null));
        map.put("lastChanged", Objects.toString(getLastChanged(), null));
        return map;
    }

    public PersonWorkTimePattern copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new PersonWorkTimePattern(merged);
    }

    private Date getLocalDateFromString(String datetime) {
        if (datetime == null) {
            return null;
        }
        // LocalDate uses host time, we need to provide the machine timezone to convert back to UTC
        return Date.from(LocalDate.parse(datetime).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private String getLocalDateStringFromDate(Instant date) {
        return LocalDate.from(date.atZone(ZoneOffset.UTC)).toString();
    }

    public UUID getId() {
        return id;
    }

    public Instant getStartDate() {
        return startDate.toInstant();
    }

    public Instant getEndDate() {
        return endDate.toInstant();
    }

    public UUID getWorkTimePattern() {
        return workTimePattern;
    }

    public Instant getLastChanged() {
        return lastChanged.toInstant();
    }

    public UUID getPerson() {
        return person;
    }
}
