package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

public class ResourceIdsFilterDto {
    public static final int DEFAULT_RESOURCE_IDS_LIMIT = 100;

    public static final boolean DEFAULT_INCLUDE_INTERNAL_PERSONS = true;

    public static final boolean DEFAULT_INCLUDE_CROWD_PERSONS = false;

    @Schema(description = "List of resourceIds that should be filtered")
    private List<String> ids;

    @Schema(description = "List of mandatory skill the persons will be filtered on")
    private List<String> skills;

    @Schema(description = "Include persons who are NON_CROWD, default is true")
    private Boolean includeInternalPersons;

    @Schema(description = "Include persons who are CROWD, default is false")
    private Boolean includeCrowdPersons;

    @Schema(description = "Limit the number of returned technicians, defaults to 100")
    private Integer limit;

    public ResourceIdsFilterDto(
        @Schema(description = "List of resourceIds that should be filtered") List<String> ids,
        @Schema(description = "List of mandatory skill the persons will be filtered on") List<String> skills,
        @Schema(description = "Include persons who are NON_CROWD, default is true") Boolean includeInternalPersons,
        @Schema(description = "Include persons who are CROWD, default is false") Boolean includeCrowdPersons,
        @Schema(description = "Limit the number of returned technicians, defaults to 100") Integer limit) {
        this.ids = ids;
        this.skills = skills;
        this.includeInternalPersons = includeInternalPersons;
        this.includeCrowdPersons = includeCrowdPersons;
        this.limit = limit;
    }

    public ResourceIdsFilterDto() {
        // For Jackson
    }

    public Boolean isIncludeInternalPersons() {
        return Optional.ofNullable(includeInternalPersons).orElse(DEFAULT_INCLUDE_INTERNAL_PERSONS);
    }

    public Boolean isIncludeCrowdPersons() {
        return Optional.ofNullable(includeCrowdPersons).orElse(DEFAULT_INCLUDE_CROWD_PERSONS);
    }

    public Integer getLimit() {
        return Optional.ofNullable(limit).orElse(DEFAULT_RESOURCE_IDS_LIMIT);
    }

    public List<String> getIds() {
        return ids;
    }

    public List<String> getSkills() {
        return skills;
    }
}
