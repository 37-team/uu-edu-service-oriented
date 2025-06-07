package com.bigitcompany.cloudaireadmodel.aggregation.configuration;

import com.google.common.collect.ImmutableMap;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collections;
import java.util.Map;

public class CARMContextRunnable implements Runnable {

    private final Runnable delegate;

    private final Map<String, String> mdcContextMap;

    private final RequestAttributes context;

    private final Map<String, Object> fsmRequestContext;

    public CARMContextRunnable(Runnable delegate) {
        this.delegate = delegate;
        context = RequestContextHolder.currentRequestAttributes();
        fsmRequestContext = RequestContext.getAsMap();

        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        mdcContextMap = mdcContext != null ?
            mdcContext.entrySet().stream().filter(e -> e.getValue() != null).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)) :
            Collections.emptyMap();
    }

    @Override
    public void run() {
        try {
            if (context != null) {
                RequestContextHolder.setRequestAttributes(context);
            }
            if (!mdcContextMap.isEmpty()) {
                MDC.setContextMap(mdcContextMap);
            }
            if (fsmRequestContext.get("account-id") != null) {
                RequestContext.setAccountId((Long) fsmRequestContext.get("account-id"));
            }
            if (fsmRequestContext.get("company-id") != null) {
                RequestContext.setCompanyId((Long) fsmRequestContext.get("company-id"));
            }
            delegate.run();
        } finally {
            MDC.clear();
            RequestContextHolder.resetRequestAttributes();
            RequestContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
