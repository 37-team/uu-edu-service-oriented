package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

public record Proficiency(Integer level) {

    //TODO FSMCPB-91040: remove hardcoded values
    public static Proficiency TECHNICIAN_PROFICIENCY = new Proficiency(99);
    public static Proficiency REQUIRED_PROFICIENCY =  new Proficiency(1);

}
