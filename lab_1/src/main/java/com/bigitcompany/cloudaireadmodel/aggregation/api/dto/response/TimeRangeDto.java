package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;

public class TimeRangeDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:00")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:00")
    private LocalTime endTime;

    public TimeRangeDto() {
    }

    public TimeRangeDto(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}