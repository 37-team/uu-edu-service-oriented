package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ExecutionStage;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record ActivityQueryApi(UUID id,
                               String activityExternalId,
                               UUID serviceCallId,
                               String serviceCallExternalId,
                               UUID equipmentId,
                               String equipmentExternalId,
                               UUID businessPartnerId,
                               String businessPartnerExternalId,
                               Instant lastChanged,
                               Instant earliestStartDateTime,
                               Instant dueDateTime,
                               Location location,
                               Integer durationInMinutes,
                               Instant startDateTime,
                               Instant endDateTime,
                               Integer plannedDurationInMinutes,
                               Integer travelTimeToInMinutes,
                               Integer travelTimeFromInMinutes,
                               ExecutionStage executionStage,
                               List<UUID> responsibles,
                               Map<String, String> udfValues,
                               Map<String, String> equipmentUdfValues,
                               Map<String, String> businessPartnerUdfValues,
                               Map<String, String> serviceCallUdfValues,
                               String syncStatus,
                               String priority) {
}