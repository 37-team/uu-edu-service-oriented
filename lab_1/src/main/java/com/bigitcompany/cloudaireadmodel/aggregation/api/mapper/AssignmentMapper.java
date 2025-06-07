package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.AssignmentDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Assignment;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class AssignmentMapper {

    private AssignmentMapper() {
    }

    public static AssignmentDto toDto(Assignment assignment) {
        return new AssignmentDto(
            UuidMapper.toFsmId(assignment.getResourceId()),
            assignment.getStartDateTime(),
            assignment.getEndDateTime()
        );
    }
}