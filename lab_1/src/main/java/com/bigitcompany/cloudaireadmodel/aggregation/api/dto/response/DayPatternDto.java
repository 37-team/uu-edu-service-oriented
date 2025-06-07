package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.util.ArrayList;
import java.util.List;

public class DayPatternDto {

    private final List<TimeRangeDto> times;

    public DayPatternDto() {
        this.times = new ArrayList<>();
    }

    public DayPatternDto(List<TimeRangeDto> times) {
        this.times = times;
    }

    public List<TimeRangeDto> getTimes() {
        return new ArrayList<>(times);
    }
}