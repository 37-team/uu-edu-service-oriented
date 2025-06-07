package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.SkillDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;

import java.util.List;
import java.util.stream.Collectors;

public class SkillMapper {

    private SkillMapper() {
    }

    public static SkillDto toDto(Skill skill) {
        return new SkillDto(
            skill.name(),
            WorkTimePatternMapper.toDto(skill.validityPattern()),
            skill.proficiency()
        );
    }

    public static List<SkillDto> toDtos(List<Skill> skills) {
        return skills.stream().map(SkillMapper::toDto).collect(Collectors.toList());
    }
}
