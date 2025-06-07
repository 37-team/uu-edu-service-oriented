package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.DayPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.TimeRange;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WeekPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SkillWorkTimePatternMapper {

    private static final List<String> DEFAULT_DAYS = List.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
    );

    private SkillWorkTimePatternMapper() {
    }

    public static WorkTimePattern toWorkTimePattern(Instant startDate, Instant endDate, LocalTime startTime, LocalTime endTime, List<String> days) {
        if (startDate == null) {
            startDate = Instant.EPOCH;
        }
        if (endDate == null) {
            endDate = Instant.parse("9999-12-31T23:59:59Z");
        }
        if (startTime == null || endTime == null || days == null) {
            return new WorkTimePattern(startDate, endDate, Collections.emptyList());
        }
        if (days.isEmpty()){
            days.addAll(DEFAULT_DAYS);
        }

        List<WeekPattern> weekPattern = new ArrayList<>();
        List<DayPattern> dayPattern = new ArrayList<>();
        for(var day: DEFAULT_DAYS) {
            List<TimeRange> times = new ArrayList<>();
            if (days.contains(day)) {
                times.add(new TimeRange(startTime, endTime));
            }
            dayPattern.add(new DayPattern(times));
        }
        weekPattern.add(new WeekPattern(dayPattern));
        return new WorkTimePattern(startDate, endDate, weekPattern);
    }
}