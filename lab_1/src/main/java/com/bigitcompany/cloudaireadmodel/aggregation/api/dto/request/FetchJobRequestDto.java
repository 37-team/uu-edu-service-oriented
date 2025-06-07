package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import java.util.Map;
import java.util.Set;

public class FetchJobRequestDto extends FetchRequestDto {

    public FetchJobRequestDto() {
        super();
    }

    public FetchJobRequestDto(Map<String, Set<String>> udfs) {
        super(udfs);
    }
}
