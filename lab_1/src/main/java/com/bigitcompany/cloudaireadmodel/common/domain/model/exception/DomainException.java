package com.bigitcompany.cloudaireadmodel.common.domain.model.exception;

/**
 * All messages inside this exception must be safe to show to end users, including customers.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
