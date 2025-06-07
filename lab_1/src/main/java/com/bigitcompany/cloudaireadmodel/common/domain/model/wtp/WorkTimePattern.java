package com.bigitcompany.cloudaireadmodel.common.domain.model.wtp;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Represents a technician's working time for a given period.
 */
public class WorkTimePattern {
    private final Instant start;

    private final Instant end;

    private final List<WeekPattern> weeks;

    public WorkTimePattern(Instant start, Instant end, List<WeekPattern> weeks) {
        this.start = start;
        this.end = end;
        this.weeks = weeks;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public List<WeekPattern> getWeeks() {
        return weeks;
    }

    @Override
    public String toString() {
        return "WorkTimePattern{" +
                "start=" + start +
                ", end=" + end +
                ", weeks=" + weeks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkTimePattern that = (WorkTimePattern) o;
        return Objects.equals(getStart(), that.getStart()) && Objects.equals(getEnd(), that.getEnd()) && Objects.equals(weeks, that.weeks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd(), weeks);
    }
}