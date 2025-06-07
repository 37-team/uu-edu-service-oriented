package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Assignment;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Job;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobValidationError;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.JobValidationResult;

import java.util.Objects;

public class JobValidationService {

    public JobValidationResult validateOptimizationJob (Job job) {
        if (job.getActivity().getId() == null) {
            return new JobValidationResult(false, JobValidationError.DATA_MISMATCH);
        } else if (job.getServiceCallId() == null) {
             return new JobValidationResult(false, JobValidationError.ACTIVITY_MISSING_SERVICE_CALL);
        } else if (job.getDurationMinutes() == null)  {
             return new JobValidationResult(false, JobValidationError.ACTIVITY_DURATION_CANNOT_BE_CALCULATED);
        } else if (job.getEarliestStartDateTime() == null) {
             return new JobValidationResult(false, JobValidationError.ACTIVITY_MISSING_EARLIEST_START_DATE);
        } else if (job.getCurrentAssignmentStatus() != null && !isValidAssignment(job.getCurrentAssignmentStatus())) {
            return new JobValidationResult(false, JobValidationError.CURRENT_ASSIGNMENT_MISSING_START_OR_END_DATE);
        } else {
            return new JobValidationResult(true);
        }
    }

    private static boolean isValidAssignment(Assignment currentAssignmentStatus) {
        if (currentAssignmentStatus.getResourceId() != null) {
            // start and end dates must exist if resource exists
            return Objects.nonNull(
                    currentAssignmentStatus.getStartDateTime()
            ) && Objects.nonNull(
                    currentAssignmentStatus.getEndDateTime()
            );
        } else {
            return true;
        }
    }

}

