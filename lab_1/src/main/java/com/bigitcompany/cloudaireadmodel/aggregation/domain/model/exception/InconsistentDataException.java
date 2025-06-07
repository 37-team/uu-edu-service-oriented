package com.bigitcompany.cloudaireadmodel.aggregation.domain.model.exception;

import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;

public class InconsistentDataException extends DomainException {
    public InconsistentDataException(String message) {
        super(message);
    }
}
