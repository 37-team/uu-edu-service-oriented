package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ResourceSkillDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ResourceSkillMapper {

    private ResourceSkillMapper() {
    }

    public static ResourceSkillDto toDto(UUID personId, List<Skill> skills) {
        return new ResourceSkillDto(UuidMapper.toFsmId(personId), SkillMapper.toDtos(skills));
    }

    public static List<ResourceSkillDto> toDtos(Map<UUID, List<Skill>> skillsByPersonIdMap) {
        return skillsByPersonIdMap.entrySet().stream()
            .map(entry -> toDto(entry.getKey(), entry.getValue()))
            .toList();
    }
}