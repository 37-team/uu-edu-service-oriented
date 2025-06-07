package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class ResourceOptionsDto {
    @JsonProperty("geocodedOnly")
    @Schema(type = "boolean", example = "false")
    private Boolean geocodedOnly;
    @JsonProperty("includeInternalPersons")
    @Schema(type = "boolean", example = "true")
    private Boolean includeInternalPersons;
    @JsonProperty("includeCrowdPersons")
    @Schema(type = "boolean", example = "false")
    private Boolean includeCrowdPersons;
    @JsonProperty("holidaysEnabled")
    @Schema(type = "boolean", example = "true")
    private Boolean holidaysEnabled;
    @JsonProperty("cacheHolidays")
    @Schema(type = "boolean", example = "true")
    private Boolean cacheHolidays;

    public ResourceOptionsDto() {
        // base default constructor
    }

    public ResourceOptionsDto(Boolean geocodedOnly,
                              Boolean includeInternalPersons,
                              Boolean includeCrowdPersons,
                              Boolean holidaysEnabled,
                              Boolean cacheHolidays) {
        this.geocodedOnly = geocodedOnly;
        this.includeInternalPersons = includeInternalPersons;
        this.includeCrowdPersons = includeCrowdPersons;
        this.holidaysEnabled = holidaysEnabled;
        this.cacheHolidays = cacheHolidays;
    }

    public Boolean isGeocodedOnly() {
        return geocodedOnly;
    }

    public Boolean isIncludeInternalPersons() {
        return includeInternalPersons;
    }

    public Boolean isIncludeCrowdPersons() {
        return includeCrowdPersons;
    }

    public Boolean isHolidaysEnabled() {
        return holidaysEnabled;
    }

    public Boolean isCacheHolidays() {
        return cacheHolidays;
    }
}
