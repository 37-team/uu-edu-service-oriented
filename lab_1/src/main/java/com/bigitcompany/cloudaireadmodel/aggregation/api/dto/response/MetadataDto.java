package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(title = "Metadata DTO", description = "Object containing information about the data stored / processed in the read model service.")
public class MetadataDto {

    @Schema(title = "Account ID", example = "1234")
    private final Long accountId;

    @Schema(title = "Company ID", example = "9876")
    private final Long companyId;

    @Schema(
        title = "Count map",
        description = "Number of object stored per supported FSM DTO.",
        example = "{ \"Tag\": 5, \"Skill\": 7, \"Requirement\": 54, \"Activity\": 30, \"Person\": 7 }"
    )
    private final Map<String, Integer> count;

    public MetadataDto(Long accountId, Long companyId, Map<String, Integer> map) {
        this.count = map;
        this.accountId = accountId;
        this.companyId = companyId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public Map<String, Integer> getCount() {
        return count;
    }
}
