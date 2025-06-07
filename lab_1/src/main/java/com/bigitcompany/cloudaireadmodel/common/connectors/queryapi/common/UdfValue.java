package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record UdfValue(String meta, String name, String value) implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public String getMeta() {
        return meta;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static Map<String, String> udfValuesToMap(List<UdfValue> udfValues) {
        Map<String, String> udfValueMap = new HashMap<>();
        if (udfValues != null) {
            for (UdfValue udf : udfValues) {
                if (udf.getName() != null) {
                    if (udfValueMap.containsKey(udf.getName())) {
                        LOG.warn("Duplicate udf name returned from queryApi: {}.", udf.getName());
                    }
                    udfValueMap.put(udf.getName(), udf.getValue());
                }
            }
        }
        return udfValueMap;
    }
}
