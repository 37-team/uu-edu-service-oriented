package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.JobIdsPartitionResponseDto;

import java.util.List;
import java.util.Map;

public final class JobIdsPartitionMapper {

    private JobIdsPartitionMapper() {
        // Utility class
    }

    public static List<JobIdsPartitionResponseDto> fromMapToDtos(Map<String, List<String>> jobIdsByRequirementName) {
        return jobIdsByRequirementName.entrySet().stream()
            .map(entry -> new JobIdsPartitionResponseDto(entry.getKey(), entry.getValue()))
            .toList();
    }
}
