package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.time.LocalDate;

public class HolidayDto {

    private final LocalDate day;

    private final DayTypeDto dayType;

    public HolidayDto(LocalDate day, DayTypeDto dayType) {
        this.day = day;
        this.dayType = dayType;
    }

    public LocalDate getDay() {
        return day;
    }

    public DayTypeDto getDayType() {
        return dayType;
    }
}