package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.JobIdsFilterDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobIdsFilter;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

import java.util.Collections;

public final class JobIdsFilterMapper {

    private JobIdsFilterMapper() {
    }

    public static JobIdsFilter toJobsIdsFilter(JobIdsFilterDto dto) {
        if (dto == null) {
            return new JobIdsFilter();
        } else {
            return new JobIdsFilter(
                dto.ids() != null ? UuidMapper.toUUIDs(dto.ids(), true) : Collections.emptyList(),
                dto.requirementNames()
            );
        }
    }
}
