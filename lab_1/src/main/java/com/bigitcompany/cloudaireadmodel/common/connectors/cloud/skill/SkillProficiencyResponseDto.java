package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.skill;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SkillProficiencyResponseDto {

    private final List<SkillProficiency> content;

    @JsonCreator
    public SkillProficiencyResponseDto(@JsonProperty("content") List<SkillProficiency> content) {
        this.content = content;
    }

    public List<SkillProficiency> getContent() {
        return content;
    }

    public static class SkillProficiency {
        private final String id;

        private final Integer proficiencyLevel;

        @JsonCreator
        public SkillProficiency(@JsonProperty("id") String id, @JsonProperty("proficiencyLevel") Integer proficiencyLevel) {
            this.id = id;
            this.proficiencyLevel = proficiencyLevel;
        }

        public String getId() {
            return id;
        }

        public Integer getProficiencyLevel() {
            return proficiencyLevel;
        }

    }

}
