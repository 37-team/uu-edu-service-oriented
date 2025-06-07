package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.util.List;

public record JobIdsPartitionResponseDto(String requirementName, List<String> ids) {
}
