package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Entity {
    private final UUID id;

    private final String externalId;

    private final Map<String, String> udfValues;

    public Entity(UUID id, String externalId, Map<String, String> udfValues) {
        this.id = id;
        this.externalId = externalId;
        this.udfValues = MapUtility.copy(udfValues);
    }

    public UUID getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    /**
     * Note: returns a read-only collection, to update state a mutator method should be used.
     */
    public Map<String, String> getUdfValues() {
        return Collections.unmodifiableMap(udfValues);
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id) && Objects.equals(externalId, entity.externalId) && Objects.equals(udfValues, entity.udfValues);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, udfValues);
    }

    @Generated
    @Override
    public String toString() {
        return "Entity{" +
            "id=" + id +
            ", externalId='" + externalId + '\'' +
            ", udfValues=" + udfValues +
            '}';
    }
}