package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class AdditionalDataOptionsRequestDto {

    @JsonProperty("useExcludeList")
    @Schema(type = "boolean", example = "false")
    private boolean useExcludeList;

    public AdditionalDataOptionsRequestDto() {
        // base default constructor
    }

    public AdditionalDataOptionsRequestDto(boolean useExcludeList) {
        this.useExcludeList = useExcludeList;
    }

    public boolean useExcludeList() {
        return useExcludeList;
    }
}
