package com.bigitcompany.cloudaireadmodel.common.domain.model;

import java.util.Locale;

public enum SupportedFsmDtos {
    ACTIVITY("Activity", 37),
    ADDRESS("Address", 15),
    BUSINESSPARTNER("BusinessPartner", 24),
    EQUIPMENT("Equipment", 24),
    PERSON("Person", 23),
    PERSONRESERVATION("PersonReservation", 20),
    PERSONWORKTIMEPATTERN("PersonWorkTimePattern", 9),
    REQUIREMENT("Requirement", 8),
    SERVICECALL("ServiceCall", 21),
    SKILL("Skill", 10),
    TAG("Tag", 10),
    UDFMETA("UdfMeta", 19),
    WORKTIME("WorkTime", 15),
    WORKTIMEPATTERN("WorkTimePattern", 8);

    private final String name;

    private final int version;

    SupportedFsmDtos(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public static SupportedFsmDtos getByName(String name) {
        return SupportedFsmDtos.valueOf(name.toUpperCase(Locale.ROOT));
    }

    public String getName() {
        return name;
    }

    public String getTableName() {
        return name.toLowerCase();
    }

    public String getFsmDtoVersion() {
        return name + "." + version;
    }
}