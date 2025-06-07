package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.holiday;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HolidayDto {

    private final String name;

    private final String date;

    private final String halfDayType;

    private final String timeZoneId;

    @JsonCreator
    public HolidayDto(@JsonProperty("name") String name,
                      @JsonProperty("date") String date,
                      @JsonProperty("halfDayType") String halfDayType,
                      @JsonProperty("timeZoneId") String timeZoneId) {
        this.name = name;
        this.date = date;
        this.halfDayType = halfDayType;
        this.timeZoneId = timeZoneId;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getHalfDayType() {
        return halfDayType;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }
}
