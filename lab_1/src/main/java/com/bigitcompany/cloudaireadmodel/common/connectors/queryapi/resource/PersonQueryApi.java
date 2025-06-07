package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PersonQueryApi(UUID id,
                             String personExternalId,
                             Integer maxDistanceRadius,
                             Location location,
                             Instant locationLastUserChangedDate,
                             String crowdType,
                             Map<String, String> udfValues) {
}
