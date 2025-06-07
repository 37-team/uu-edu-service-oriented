package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.EquipmentDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class EquipmentMapper {

    private EquipmentMapper() {
    }

    public static EquipmentDto toDto(Entity equipment) {
        if (equipment == null || equipment.getId() == null) {
            return null;
        }
        return new EquipmentDto(UuidMapper.toFsmId(equipment.getId()), equipment.getExternalId(), equipment.getUdfValues());
    }
}