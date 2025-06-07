package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

public class ValidatedJobs {

    @NotNull
    private final List<Job> validJobs;

    @NotNull
    private final List<InvalidJob> invalidJobs;

    public ValidatedJobs(List<Job> validJobs, List<InvalidJob> invalidJobs) {
        this.validJobs = validJobs;
        this.invalidJobs = invalidJobs;
    }

    /**
      * Note: returns a read-only collection, to update state a mutator method should be used.
      */
    public List<Job> getValidJobs() {
        return Collections.unmodifiableList(validJobs);
    }

    /**
      * Note: returns a read-only collection, to update state a mutator method should be used.
      */
    public List<InvalidJob> getInvalidJobs() {
        return Collections.unmodifiableList(invalidJobs);
    }
}