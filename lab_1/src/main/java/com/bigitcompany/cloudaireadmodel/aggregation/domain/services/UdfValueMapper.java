package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.sap.fsm.data.event.common.UdfValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UdfValueMapper {

    private UdfValueMapper() { }

    public static Map<String, String> udfValuesToMap(List<UdfValue> udfValues) {
        if (udfValues != null) {
            return udfValues.stream().filter(
                udfValue -> udfValue.getKey() != null
            ).collect(
                Collectors.toMap(
                    UdfValue::getKey,
                    UdfValue::getValue
                )
            );
        } else {
            return Collections.emptyMap();
        }
    }
}
