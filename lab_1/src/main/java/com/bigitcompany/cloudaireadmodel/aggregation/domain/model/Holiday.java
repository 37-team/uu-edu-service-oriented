package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.time.LocalDate;

public class Holiday {

    private final LocalDate day;

    private final DayType dayType;

    public Holiday(LocalDate day, DayType dayType) {
        this.day = day;
        this.dayType = dayType;
    }

    public LocalDate getDay() {
        return day;
    }

    public DayType getDayType() {
        return dayType;
    }
}