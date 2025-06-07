package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

public class JobValidationResult {

    private boolean isValid;
    private String reason;

    public JobValidationResult(boolean isValid, JobValidationError reason) {
        this.isValid = isValid;
        this.reason = reason.name();
    }

    public JobValidationResult(boolean isValid) {
        this.isValid = isValid;
        this.reason = "";
    }


    public boolean isValid() {
        return isValid;
    }

    public String getReason() {
        return reason;
    }
}
