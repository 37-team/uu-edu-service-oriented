package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.RequirementDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Requirement;

import java.util.List;

public class RequirementMapper {

    private RequirementMapper() {
    }

    public static RequirementDto toDto(Requirement requirement) {
        return new RequirementDto(requirement.tag(), requirement.proficiency(), requirement.mandatory());
    }

    public static List<RequirementDto> toDtos(List<Requirement> requirements) {
        return requirements.stream().map(RequirementMapper::toDto).toList();
    }

}
