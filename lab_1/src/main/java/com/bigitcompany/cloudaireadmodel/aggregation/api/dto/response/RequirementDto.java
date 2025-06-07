package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Proficiency;

public class RequirementDto {

    private String skillName;

    private Proficiency proficiency;

    private boolean mandatory;


    public RequirementDto() {
        // for jackson
    }

    public RequirementDto(String skillName, Proficiency proficiency, boolean mandatory) {
        this.skillName = skillName;
        this.proficiency = proficiency;
        this.mandatory = mandatory;
    }

    public String getSkillName() {
        return skillName;
    }
    public Proficiency getProficiency() {
        return proficiency;
    }
    public boolean isMandatory() {
        return mandatory;
    }
}
