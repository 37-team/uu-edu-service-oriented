package com.bigitcompany.cloudaireadmodel.common.domain.model.wtp;
import com.sap.fsm.data.event.common.WorkTimePatternWeekDay;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Class representing technician's working hours for a single day.
 */
public class DayPattern {

    private final List<TimeRange> times;

    public DayPattern() {
        times = new ArrayList<>();
    }

    public DayPattern(List<TimeRange> times) {
        this.times = times;
    }

    public DayPattern(WorkTimePatternWeekDay day) {
        times = new ArrayList<>();
        for(com.sap.fsm.data.event.common.TimeRange tm: day.getTimes()) {
            times.add(new TimeRange(LocalTime.parse(tm.getStartTime()), LocalTime.parse(tm.getEndTime())));
        }
    }

    public List<TimeRange> getTimes() {
        return new ArrayList<>(times);
    }

    @Override
    public String toString() {
        return "DayPattern{" +
                "times=" + times +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DayPattern that = (DayPattern) o;
        return Objects.equals(times, that.times);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(getTimes().toArray(new TimeRange[0]));
    }
}