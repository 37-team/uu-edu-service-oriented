package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.DayPatternDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.TimeRangeDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.WeekPatternDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.WorkTimePatternDto;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.DayPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.TimeRange;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WeekPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;

import java.util.List;

/**
 * This class maps WorkTimePattern objects to its dto representation and all downstream objects that define WTPs
 */
public class WorkTimePatternMapper {

    private WorkTimePatternMapper() {
    }

    public static List<WorkTimePatternDto> toDtos(List<WorkTimePattern> workTimePatterns) {
        return workTimePatterns.stream().map(WorkTimePatternMapper::toDto).toList();
    }

    public static WorkTimePatternDto toDto(WorkTimePattern workTimePattern) {
        return new WorkTimePatternDto(
            workTimePattern.getStart(),
            workTimePattern.getEnd(),
            workTimePattern.getWeeks().stream().map(WorkTimePatternMapper::toWeekPatternDto).toList()
        );
    }

    private static WeekPatternDto toWeekPatternDto(WeekPattern weekPattern) {
        return new WeekPatternDto(
            weekPattern.getDays().stream().map(WorkTimePatternMapper::toDayPatternDto).toList()
        );
    }

    private static DayPatternDto toDayPatternDto(DayPattern dayPattern) {
        return new DayPatternDto(
            dayPattern.getTimes().stream().map(WorkTimePatternMapper::toTimeRangeDto).toList()
        );
    }

    private static TimeRangeDto toTimeRangeDto(TimeRange timeRange) {
        return new TimeRangeDto(timeRange.getStartTime(), timeRange.getEndTime());
    }
}