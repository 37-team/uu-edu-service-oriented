package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
public class JobRequestDto {

    @JsonProperty("jobIds")
    @Schema(
                    type="array",
                    example = "[ " +
                            "\"242999B5EA6A4ADFBE2DC38EB8A5AD5C\", " +
                            "\"242999b5-ea6a-4adf-be2d-c38eb8a5ad5c\" " +
                            "]",
                    description = "Can be FSM IDs or UUIDs"
            )
    private List<String> jobIds;
    @JsonProperty("additionalDataOptions")
    @Schema(type = "object")
    private AdditionalDataOptionsRequestDto additionalDataOptions;
    @JsonProperty("fetch")
    @Schema(implementation = FetchJobRequestDto.class,
            example = "{" +
            "      \"JOB\": [" +
            "        \"urgency\"" +
            "      ]," +
            "      \"EQUIPMENT\": [" +
            "        \"repairWeeklySchedule\"," +
            "        \"priority\"" +
            "      ]," +
            "      \"BUSINESSPARTNER\": [" +
            "        \"partnerGroup\"" +
            "      ]" +
            "    }")
    private FetchJobRequestDto fetch;

    public JobRequestDto() {
        // base default constructor
    }

    public JobRequestDto(List<String> jobIds, AdditionalDataOptionsRequestDto additionalDataOptions, FetchJobRequestDto fetch) {
        this.jobIds = jobIds;
        this.additionalDataOptions = additionalDataOptions;
        this.fetch = fetch;
    }

    public List<String> getJobIds() {
        return jobIds;
    }

    public AdditionalDataOptionsRequestDto getAdditionalDataOptions() {
        return additionalDataOptions;
    }

    public FetchJobRequestDto getFetch() {
        return fetch;
    }
}
