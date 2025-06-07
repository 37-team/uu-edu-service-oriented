package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "Base wrapper for api response")
public class ApiResponseDto<T> {

    private List<T> results;

    public ApiResponseDto() {
        // base default constructor
    }

    public ApiResponseDto(List<T> results) {
        this.results = results;
    }

    public List<T> getResults() {
        return results;
    }

}
