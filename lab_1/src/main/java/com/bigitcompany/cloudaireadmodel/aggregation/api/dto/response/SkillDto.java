package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Proficiency;

public class SkillDto {

    private String name;

    private WorkTimePatternDto validityPattern;

    private Proficiency proficiency;


    public SkillDto() {
        // for jackson
    }

    public SkillDto(String name, WorkTimePatternDto validityPattern, Proficiency proficiency) {
        this.name = name;
        this.validityPattern = validityPattern;
        this.proficiency = proficiency;
    }

    public String getName() {
        return name;
    }

    public WorkTimePatternDto getValidityPattern() {
        return validityPattern;
    }

    public Proficiency getProficiency() {
        return proficiency;
    }
}
