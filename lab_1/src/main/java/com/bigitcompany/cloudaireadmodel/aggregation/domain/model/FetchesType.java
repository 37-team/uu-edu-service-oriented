package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

public enum FetchesType {
    JOB("JOB"),
    EQUIPMENT("EQUIPMENT"),
    RESOURCE("RESOURCE"),
    SKILL("SKILL"),
    BUSINESSPARTNER("BUSINESSPARTNER");


    private final String name;

    FetchesType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}