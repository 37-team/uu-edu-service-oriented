package com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UdfMapFilter {

    private final FetchRequest fetchUdfRequest;

    public UdfMapFilter(FetchRequest fetchUdfRequest) {
        this.fetchUdfRequest = fetchUdfRequest;
    }

    public Map<String, String> filterUDFs(FetchesType fetchType, Map<String, String> udfs) {
        if (fetchUdfRequest == null || fetchUdfRequest.getRequestData() == null || udfs == null) {
            return removeNullKey(udfs);
        } else if (fetchUdfRequest.doNotReturn()) {
            return Collections.emptyMap();
        } else if (fetchUdfRequest.getRequestData().get(fetchType.name()) == null) {
            return removeNullKey(udfs);
        } else {
            var filteredUdfs = new HashMap<String, String>();
            fetchUdfRequest.getRequestData().get(fetchType.name())
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(key -> udfs.get(key) != null)
                    .forEach(key -> filteredUdfs.put(key, udfs.get(key)));
            return filteredUdfs;
        }
    }

    private static Map<String, String> removeNullKey(Map<String, String> udfs) {
        if (udfs != null) {
            return udfs.entrySet().stream().filter(udfEntry -> udfEntry.getKey() != null).collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue
            ));
        } else {
            return Collections.emptyMap();
        }
    }
}
