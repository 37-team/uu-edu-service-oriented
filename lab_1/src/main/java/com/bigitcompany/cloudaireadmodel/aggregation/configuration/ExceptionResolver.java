package com.bigitcompany.cloudaireadmodel.aggregation.configuration;

import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import java.lang.invoke.MethodHandles;

@Component
public class ExceptionResolver extends AbstractHandlerExceptionResolver {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    protected ModelAndView doResolveException(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, Object handler, @NotNull Exception ex) {
        try {
            // For traceability all exceptions have to be logged here
            LOG.error(ex.getMessage(), ex);

            if (ex instanceof DomainResponseException domainResponseException) {
                response.sendError((domainResponseException).getStatusCode().value(), domainResponseException.getMessage());
            } else if (ex instanceof DomainException domainException) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, domainException.getMessage());
            } else if (ex instanceof AccessDeniedException accessDeniedException) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, accessDeniedException.getMessage());
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception occurred");
            }

            return new ModelAndView();
        } catch (Exception e) { // Catching all exception that happen during Error Response handling
            LOG.warn("Error while handling {}: {}", ex.getClass().getName(), ex.getMessage(), e);
        }
        return null;
    }
}