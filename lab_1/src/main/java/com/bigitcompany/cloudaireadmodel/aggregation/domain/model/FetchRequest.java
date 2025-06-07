package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class FetchRequest {

    private final Map<String, Set<String>> requestData;

    private final boolean doNotReturn;

    public FetchRequest(Map<String, Set<String>> requestData, boolean doNotReturn) {
        this.requestData = requestData;
        this.doNotReturn = doNotReturn;
    }

    public Map<String, Set<String>> getRequestData() {
        return requestData;
    }

    public boolean doNotReturn() {
        return doNotReturn;
    }

    public static final class Builder {
        private Map<String, Set<String>> requestData;

        private boolean doNotReturn;

        public Builder() {
            this.requestData = Collections.emptyMap();
            this.doNotReturn = false;
        }

        public FetchRequest.Builder requestData(Map<String, Set<String>> requestData) {
            if (requestData != null) {
                this.requestData = requestData;
            }
            return this;
        }

        public FetchRequest.Builder doNotReturn(Map<String, Set<String>> requestData) {
            if (requestData != null && requestData.isEmpty()) {
                this.doNotReturn = true;
            }
            return this;
        }

        public FetchRequest build() {
            return new FetchRequest(
                    this.requestData,
                    this.doNotReturn
            );
        }
    }
}
