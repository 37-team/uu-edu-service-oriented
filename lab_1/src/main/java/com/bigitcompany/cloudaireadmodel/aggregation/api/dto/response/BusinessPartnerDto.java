package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.Map;

public class BusinessPartnerDto {

    private String id;

    private String externalId;

    @Schema(
        type = "object",
        example = "{ \"partnerGroup\": \"Northern Germany\" }"
    )
    private Map<String, String> udfValues;

    public BusinessPartnerDto() {
    }

    public BusinessPartnerDto(String id, String externalId, Map<String, String> udfValues) {
        this.id = id;
        this.externalId = externalId;
        this.udfValues = udfValues;
    }

    public String getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public Map<String, String> getUdfValues() {
        return Collections.unmodifiableMap(udfValues);
    }
}