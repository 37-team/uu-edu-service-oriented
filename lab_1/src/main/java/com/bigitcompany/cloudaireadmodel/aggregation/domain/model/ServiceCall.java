package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ServiceCall extends Entity {

    private String priority;

    public ServiceCall(UUID id, String externalId, Map<String, String> udfValues, String priority) {
        super(id, externalId, udfValues);
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServiceCall that = (ServiceCall) o;
        return Objects.equals(priority, that.priority);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), priority);
    }

    @Generated
    @Override
    public String toString() {
        return "ServiceCall{" +
                "priority='" + priority + '\'' +
                '}';
    }
}