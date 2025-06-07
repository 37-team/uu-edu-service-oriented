package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WeekPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.data.event.common.WorkTimePatternWeek;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.Valid;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Valid
@Entity(name = "worktimepattern")
public final class WorkTimePattern {

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Type(JsonBinaryType.class)
    @Column(name = "weeks", columnDefinition = "jsonb")
    private List<WeekPattern> weeks;

    @Column(name = "lastChanged", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastChanged;

    public WorkTimePattern() {
    }

    public WorkTimePattern(Map<String, Object> map) {
        this.id = UuidMapper.toUUID(((String) map.get("id")));
        List<WeekPattern> weeksRm = new ArrayList<>();
        Object mapWeeks = map.get("weeks");
        if (mapWeeks instanceof List<?> mapWeeksList && (!mapWeeksList.isEmpty())) {
            for (Object j : mapWeeksList) {
                if (j instanceof WorkTimePatternWeek workTimePatternWeek) {
                    var wp = new WeekPattern(workTimePatternWeek);
                    weeksRm.add(wp);
                } else {
                    weeksRm.add(new WeekPattern(((WeekPattern) j).getDays()));
                }
            }
        }
        this.weeks = weeksRm;
        this.lastChanged = DateTimeService.toDate((String) map.get("lastChanged"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", UuidMapper.toFsmId(getId().toString()));
        map.put("weeks", getWeeks());
        map.put("lastChanged", getLastChanged());
        return map;
    }

    public WorkTimePattern copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(this.toMap(), overrides);
        return new WorkTimePattern(merged);
    }

    public UUID getId() {
        return id;
    }

    public List<WeekPattern> getWeeks() {
        return weeks;
    }

    public Date getLastChanged() {
        return lastChanged;
    }
}