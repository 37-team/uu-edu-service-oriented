package com.bigitcompany.cloudaireadmodel.persistence.events.mappers;

import com.sap.fsm.data.event.common.UdfValue;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.util.ArrayList;
import java.util.List;

public class UdfMapper {

    private UdfMapper() {
        // private constructor to prevent instantiating a class with only static methods
    }

    public static GenericData.Array<UdfValue> toGenericDataArray(List<UdfValue> udfs) {
        var schema = Schema.createArray(Schema.create(Schema.Type.BYTES));
        return new GenericData.Array<>(schema, udfs);
    }

    public static List<UdfValue> toUdfValueList(GenericData.Array<UdfValue> udfs) {
        if (udfs == null || udfs.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(udfs);
    }

}