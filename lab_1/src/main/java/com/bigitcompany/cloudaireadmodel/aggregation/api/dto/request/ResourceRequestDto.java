package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceRequestDto {

    @JsonProperty("options")
    @Schema(implementation = ResourceOptionsDto.class, type = "object")
    private ResourceOptionsDto options;

    @NotNull
    @Valid
    @JsonProperty("bookingsFilter")
    @Schema(implementation = BookingsFilterDto.class, type = "object", required = true)
    private BookingsFilterDto bookingsFilter;

    @JsonProperty("personIds")
    @Schema(
            type = "array",
            example = """
                        [ 
                        "242999B5EA6A4ADFBE2DC38EB8A5AD5C",
                        "242999b5-ea6a-4adf-be2d-c38eb8a5ad5c"
                    ]""",
            description = "Can be FSM IDs or UUIDs")
    private List<String> personIds;

    @JsonProperty("fetch")
    @Schema(implementation = FetchResourceRequestDto.class,
            example = """
            "udfs" : {    "RESOURCE": [
                    "preferredTimes"
                  ]
                  "SKILL": [
                    "workcenterPercentage"
                  ]
                },
            "skills": {    "RESOURCE": [
                    "English", "Spanish"
                  ]
                }
             """)
    private FetchResourceRequestDto fetch;

    public ResourceRequestDto() {
        // base default constructor
    }

    public ResourceRequestDto(ResourceOptionsDto options,
                              BookingsFilterDto bookingsFilter,
                              List<String> personIds,
                              FetchResourceRequestDto fetch) {
        this.options = options;
        this.bookingsFilter = bookingsFilter;
        this.personIds = personIds;
        this.fetch = fetch;
    }

    public ResourceOptionsDto getOptions() {
        return options;
    }

    public BookingsFilterDto getBookingsFilter() {
        return bookingsFilter;
    }

    public List<String> getPersonIds() {
        return personIds;
    }

    public FetchResourceRequestDto getFetch() {
        return fetch;
    }
}
