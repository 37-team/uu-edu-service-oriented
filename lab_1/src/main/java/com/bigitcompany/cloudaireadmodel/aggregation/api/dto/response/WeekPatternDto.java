package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.util.List;

public class WeekPatternDto {

    private List<DayPatternDto> days;

    public WeekPatternDto() {
    }

    public WeekPatternDto(List<DayPatternDto> days) {
        this.days = days;
    }

    public List<DayPatternDto> getDays() {
        return days;
    }
}