package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class JobResponseDto {

    @Schema(implementation = JobDto.class)
    private List<JobDto> validJobs;

    @Schema(implementation = InvalidJobDto.class)
    private List<InvalidJobDto> invalidJobs;

    @JsonCreator
    public JobResponseDto(List<JobDto> validJobs, List<InvalidJobDto> invalidJobs) {
        this.validJobs = validJobs;
        this.invalidJobs = invalidJobs;
    }

    public JobResponseDto() {
    }

    public List<JobDto> getValidJobs() {
        return validJobs;
    }

    public List<InvalidJobDto> getInvalidJobs() {
        return invalidJobs;
    }
}