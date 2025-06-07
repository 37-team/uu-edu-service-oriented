package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.util.List;

public class ResourceSkillDto {

    private String id;

    private List<SkillDto> skills;

    public ResourceSkillDto(String id, List<SkillDto> skills) {
        this.id = id;
        this.skills = skills;
    }

    public ResourceSkillDto() {
    }

    public String getId() {
        return id;
    }

    public List<SkillDto> getSkills() {
        return skills;
    }
}
