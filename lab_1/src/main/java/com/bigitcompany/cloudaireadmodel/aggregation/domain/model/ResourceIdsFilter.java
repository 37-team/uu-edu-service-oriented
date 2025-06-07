package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourceIdsFilterDto.DEFAULT_RESOURCE_IDS_LIMIT;

public record ResourceIdsFilter(List<UUID> ids, List<String> skills, boolean includeInternalPersons,
                                boolean includeCrowdPersons, Integer limit) {


    public ResourceIdsFilter() {
        this(Collections.emptyList(), Collections.emptyList(), true, false, DEFAULT_RESOURCE_IDS_LIMIT);
    }
}
