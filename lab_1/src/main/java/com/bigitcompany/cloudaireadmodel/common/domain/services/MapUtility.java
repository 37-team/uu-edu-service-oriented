package com.bigitcompany.cloudaireadmodel.common.domain.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for operations on Map objects.
 */
public class MapUtility {

    protected MapUtility() {
        throw new IllegalStateException("Static utility class should not be instantiated");
    }

    public static Map<String, Object> merge(Map<String, Object> full, Map<String, Object> overrides) {
        Map<String, Object> map = new HashMap<>(full);
        map.putAll(overrides);
        return map;
    }

    public static Map<String, String> copy(Map<String, String> original) {
        if (original == null || original.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, String> copy = new HashMap<>();
        for (Map.Entry<String, String> entry : original.entrySet()) {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}