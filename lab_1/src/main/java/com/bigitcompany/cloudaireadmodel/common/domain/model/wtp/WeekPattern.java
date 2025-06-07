package com.bigitcompany.cloudaireadmodel.common.domain.model.wtp;

import com.sap.fsm.data.event.common.WorkTimePatternWeek;
import com.sap.fsm.data.event.common.WorkTimePatternWeekDay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *  Class representing a working week of a technician.
 */
public class WeekPattern {

    private List<DayPattern> days;

    public WeekPattern(List<DayPattern> days) {
        this.days = days;
    }

    public WeekPattern(WorkTimePatternWeek workTimePatternWeek) {
        days = new ArrayList<>();
        for(WorkTimePatternWeekDay day: workTimePatternWeek.getDays()) {
            days.add(new DayPattern(day));
        }
    }

    public List<DayPattern> getDays() {
        return days;
    }

    public WeekPattern() {
        // Used by JPA
    }

    @Override
    public String toString() {
        return "ScheduledWeekPattern{" +
                "days=" + days +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekPattern that = (WeekPattern) o;
        return Objects.equals(days, that.days);
    }

    @Override
    public int hashCode() {
        return Objects.hash(days);
    }
}