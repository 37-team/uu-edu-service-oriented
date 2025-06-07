package com.bigitcompany.cloudaireadmodel.aggregation.configuration;

import com.google.common.collect.ImmutableMap;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;

public class CARMContextCallable<V> implements Callable<V> {

    private final Callable<V> delegate;

    private final Map<String, String> mdcContextMap;

    public CARMContextCallable(Callable<V> delegate) {
        this.delegate = delegate;

        final Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        mdcContextMap = mdcContext != null ? mdcContext.entrySet().stream().filter(e -> e.getValue() != null).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)) : Collections.emptyMap();
    }

    @Override
    public V call() throws Exception {
        try {
            if (!mdcContextMap.isEmpty()) {
                MDC.setContextMap(mdcContextMap);
            }
            return delegate.call();
        } finally {
            MDC.clear();
            RequestContextHolder.resetRequestAttributes();
            SecurityContextHolder.clearContext();
        }
    }
}
