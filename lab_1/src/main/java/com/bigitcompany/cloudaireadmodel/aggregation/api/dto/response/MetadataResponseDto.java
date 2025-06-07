package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "Metadata Response")
public class MetadataResponseDto extends ApiResponseDto<MetadataDto> {

    public MetadataResponseDto(MetadataDto result) {
        super(List.of(result));
    }

}
