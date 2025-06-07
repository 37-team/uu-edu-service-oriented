package com.bigitcompany.cloudaireadmodel.common.domain.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * All messages inside this exception must be safe to show to API users.
 * They must also be safe to show to end users, including customers.
 */
public class DomainResponseException extends ResponseStatusException {

    public DomainResponseException(HttpStatus status) {
        super(status);
    }

    /**
     * Important: the provided reason must be safe to show to API users and end users (including customers).
     * @param status
     * @param reason
     */
    public DomainResponseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    /**
     * Important: the provided reason must be safe to show to API users and end users (including customers).
     * @param status
     * @param reason
     * @param cause
     */
    public DomainResponseException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    /**
     * Important: the provided reason must be safe to show to API users and end users (including customers).
     * @param rawStatusCode
     * @param reason
     * @param cause
     */
    public DomainResponseException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
