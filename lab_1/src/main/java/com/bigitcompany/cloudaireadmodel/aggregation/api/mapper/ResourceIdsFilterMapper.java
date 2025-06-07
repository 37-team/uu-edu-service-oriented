package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourceIdsFilterDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourceIdsFilterDtoV2;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceIdsFilter;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

import java.util.Collections;

public final class ResourceIdsFilterMapper {

    private ResourceIdsFilterMapper() {
    }

    public static ResourceIdsFilter toResourceIdsFilter(ResourceIdsFilterDto dto) {
        if (dto == null) {
            return new ResourceIdsFilter();
        } else {
            return new ResourceIdsFilter(
                dto.getIds() != null ? UuidMapper.toUUIDs(dto.getIds(), true) : Collections.emptyList(),
                dto.getSkills(),
                dto.isIncludeInternalPersons(),
                dto.isIncludeCrowdPersons(),
                dto.getLimit());
        }
    }


    public static ResourceIdsFilter toResourceIdsFilterV2(ResourceIdsFilterDtoV2 dto) {
        return new ResourceIdsFilter(
                Collections.emptyList(),
                dto.skills(),
                dto.includeInternalPersons(),
                dto.includeCrowdPersons(),
                dto.limit()
        );
    }
}
