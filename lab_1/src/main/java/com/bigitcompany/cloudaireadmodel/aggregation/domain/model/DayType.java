package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

public enum DayType {
    FULL_DAY_OFF ("FULL_DAY_OFF"),
    FIRST_HALF_OFF ("FIRST_HALF_OFF"),
    SECOND_HALF_OFF ("SECOND_HALF_OFF");

    String type;

    DayType(String type) {
        this.type = type;
    }
}