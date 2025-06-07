package com.bigitcompany.cloudaireadmodel.persistence.events.mappers;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.List;

public class StringMapper {

    private StringMapper() {
        throw new IllegalStateException("Static utility class should not be instantiated");
    }

    public static GenericData.Array<String> toGenericDataArray(List<String> items) {
        var schema = Schema.createArray(Schema.create(Schema.Type.STRING));
        return new GenericData.Array<>(schema, items);
    }
}