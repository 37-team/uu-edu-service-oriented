package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.Set;

public class FetchResourceRequestDto extends FetchRequestDto {

    @JsonProperty("skills")
    @Schema(implementation = FetchRequestDto.class,
        description =
            "Can be null, empty or contains info. " +
                "If skills map is null - all skills for person should be returned, " +
                "if skills map is empty - none of skills should be returned, " +
                "if skills map contain any info - requested skills should be returned ")
    private Map<String, Set<String>> skills;

    public FetchResourceRequestDto() {
        super();
    }

    public FetchResourceRequestDto(Map<String, Set<String>> udfs, Map<String, Set<String>> skills) {
        super(udfs);
        this.skills = skills;
    }

    public Map<String, Set<String>> getSkills() {
        return skills;
    }
}
