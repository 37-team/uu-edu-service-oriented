package com.bigitcompany.cloudaireadmodel.common.tracing;

import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TracingService {

    @Autowired
    private Tracer tracer;

    public void eventOnCurrentSpan(String value) {
        var currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            Objects.requireNonNull(tracer.currentSpan()).event(value);
        }
    }

    public void tagOnCurrentSpan(String tag, String value) {
        var currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag(tag, value);
        }
    }

    public void tagOnCurrentSpan(String tag, Integer value) {
        tagOnCurrentSpan(tag, String.valueOf(value));
    }
}
