package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.util.UUID;

public class Udf {

    private final UUID entityId;
    private final UUID metaId;
    private final String name;
    private final String value;

    public Udf(UUID entityId, UUID metaId, String name, String value) {
        this.entityId = entityId;
        this.metaId = metaId;
        this.name = name;
        this.value = value;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public UUID getMetaId() {
        return metaId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {
        private UUID entityId;
        private UUID metaId;
        private String name;
        private String value;

        public Builder entityId(UUID entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder metaId(UUID metaId) {
            this.metaId = metaId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Udf build() {
            return new Udf(this.entityId, this.metaId, this.name, this.value);
        }
    }
}
