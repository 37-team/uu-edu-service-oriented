package com.bigitcompany.cloudaireadmodel.persistence.domain.services;

import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapMapper {

    private MapMapper() {
        super();
    }

    public static Map<String, Object> fromRecord(SpecificRecordBase sourceOfValues) {
        Map<String, Object> map = new HashMap<>();
        List<Schema.Field> fields = sourceOfValues.getSchema().getFields();
        for (Schema.Field field : fields) {
            map.put(field.name(), sourceOfValues.get(field.name()));
        }
        return map;
    }
}
