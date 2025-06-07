package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;

public class RowMapperHelper {

    private RowMapperHelper() {
        throw new IllegalStateException("Utility classes cannot be instantiated!");
    }

    public static <T> T mapPgObjectTo(Object value, Class<T> clazz) throws JsonProcessingException {
        if (value == null || value.toString().equals("{}")) {
            return null;
        } else {
            var pGobject = ((PGobject) value);
            return new ObjectMapper().readValue(pGobject.getValue(), clazz);
        }
    }
}