package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.bigitcompany.cloudaireadmodel.persistence.events.mappers.DayMapper;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.avro.generic.GenericData;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "skill")
public class Skill {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "tag")
    private UUID tag;

    @Column(name = "person")
    private UUID person;

    @Column(name = "startDate")
    private String startDate;

    @Column(name = "endDate")
    private String endDate;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "endTime")
    private String endTime;

    @Type(JsonBinaryType.class)
    @Column(name = "days", columnDefinition = "jsonb")
    private List<String> days;

    @Column(name = "lastChanged")
    private Instant lastChanged;

    public static final String ID_KEY = "id";

    public static final String TAG_KEY = "tag";

    public static final String PERSON_KEY = "person";

    public static final String START_DATE_KEY = "startDate";

    public static final String END_DATE_KEY = "endDate";

    public static final String START_TIME_KEY = "startTime";

    public static final String END_TIME_KEY = "endTime";

    public static final String DAYS_KEY = "days";

    public static final String LAST_CHANGED_KEY = "lastChanged";

    public Skill() {
        // no-args constructor
    }

    public Skill(Map<String, Object> map) {
        this.id = UuidMapper.toUUID((String) map.get(ID_KEY));
        this.tag = UuidMapper.toUUID((String) map.get(TAG_KEY));
        this.person = UuidMapper.toUUID((String) map.get(PERSON_KEY));

        this.startDate = (String) map.get(START_DATE_KEY);
        this.endDate = (String) map.get(END_DATE_KEY);

        this.startTime = (String) map.get(START_TIME_KEY);
        this.endTime = (String) map.get(END_TIME_KEY);

        this.days = DayMapper.toDayValueList((GenericData.Array<String>) map.get(DAYS_KEY));

        this.lastChanged = DateTimeService.toInstant((String) map.get(LAST_CHANGED_KEY));
    }

    public UUID getId() {
        return id;
    }

    public UUID getTag() {
        return tag;
    }

    public UUID getPerson() {
        return person;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public List<String> getDays() {
        return days;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(ID_KEY, UuidMapper.toFsmId(getId().toString()));
        map.put(TAG_KEY, UuidMapper.toFsmId(getTag()));
        map.put(PERSON_KEY, UuidMapper.toFsmId(getPerson()));
        map.put(START_DATE_KEY, getStartDate());
        map.put(END_DATE_KEY, getEndDate());
        map.put(START_TIME_KEY, getStartTime());
        map.put(END_TIME_KEY, getEndTime());
        map.put(DAYS_KEY, DayMapper.toGenericDataArray(getDays()));
        map.put(LAST_CHANGED_KEY, getLastChanged() == null ? null : getLastChanged().toString());
        return map;
    }

    public Skill copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new Skill(merged);
    }
}
