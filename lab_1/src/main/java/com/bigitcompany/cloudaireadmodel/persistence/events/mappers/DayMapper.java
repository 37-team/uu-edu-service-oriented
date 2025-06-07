package com.bigitcompany.cloudaireadmodel.persistence.events.mappers;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.ArrayList;
import java.util.List;

public class DayMapper {

    private DayMapper() {
        // private constructor to prevent instantiating a class with only static methods
    }

    public static GenericData.Array<String> toGenericDataArray(List<String> days) {
        var schema = Schema.createArray(Schema.create(Schema.Type.BYTES));
        return new GenericData.Array<>(schema, days);
    }

    public static List<String> toDayValueList(GenericData.Array<String> days) {
        if (days == null || days.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(days);
    }

}