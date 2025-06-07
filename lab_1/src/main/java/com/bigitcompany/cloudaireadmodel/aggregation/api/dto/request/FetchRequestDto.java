package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.Set;

public class FetchRequestDto {

    @JsonProperty("udfs")
    @Schema(implementation = FetchRequestDto.class,
        description =
            "Can be null, empty or contains info. " +
                "If udfs map is null - all udfs for company should be returned, " +
                "if udfs map is empty - none of udfs should be returned, " +
                "if udfs map contain any info - requested udfs should be returned ")
    private Map<String, Set<String>> udfs;

    public FetchRequestDto() {
    }

    public FetchRequestDto(Map<String, Set<String>> udfs) {
        this.udfs = udfs;
    }

    public Map<String, Set<String>> getUdfs() {
        return udfs;
    }

}
