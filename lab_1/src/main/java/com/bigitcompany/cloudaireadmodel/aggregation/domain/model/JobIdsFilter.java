package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record JobIdsFilter(List<UUID> ids, List<String> requirements) {
    public JobIdsFilter() {
        this(Collections.emptyList(), Collections.emptyList());
    }
}
