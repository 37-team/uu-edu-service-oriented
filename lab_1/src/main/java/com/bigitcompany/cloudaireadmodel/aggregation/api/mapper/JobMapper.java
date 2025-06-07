package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ExecutionStageDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.InvalidJobDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.JobDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.JobResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.InvalidJob;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Job;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ValidatedJobs;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class JobMapper {

    private JobMapper() {
    }

    public static JobResponseDto toJobResponseDTO(ValidatedJobs validatedJobs) {
        return new JobResponseDto(
            validatedJobs.getValidJobs().stream().map(JobMapper::toJobDto).toList(),
            validatedJobs.getInvalidJobs().stream().map(JobMapper::toInvalidJobDto).toList()
        );
    }

    private static JobDto toJobDto(Job job) {
        return new JobDto(
                UuidMapper.toFsmId(job.getId()),
                UuidMapper.toFsmId(job.getServiceCallId()),
                job.getActivity() != null ? ActivityMapper.toDto(job.getActivity()) : null,
                job.getEarliestStartDateTime(),
                job.getDueDateTime(),
                job.getLocation() != null ? LocationMapper.toDto(job.getLocation()) : null,
                job.getOptionalRequirements(),
                job.getMandatoryRequirements(),
                RequirementMapper.toDtos(job.getRequirements()),
                job.getDurationMinutes(),
                job.getUdfValues(),
                job.getResourcesToExclude().stream().map(UuidMapper::toFsmId).toList(),
                job.getExecutionStage() != null ? ExecutionStageDto.valueOf(job.getExecutionStage().name()) : null,
                EquipmentMapper.toDto(job.getEquipment()),
                BusinessPartnerMapper.toDto(job.getBusinessPartner()),
                job.getSyncStatus(),
                job.getCurrentAssignmentStatus() != null ? AssignmentMapper.toDto(job.getCurrentAssignmentStatus()) : null,
                job.getPriority()
        );
    }

    private static InvalidJobDto toInvalidJobDto(InvalidJob invalidJob) {
        return new InvalidJobDto(
            UuidMapper.toFsmId(invalidJob.getId()),
            invalidJob.getLastChanged(),
            invalidJob.getReason()
        );
    }
}
