package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.AdditionalDataOptionsRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.AdditionalDataOptionsRequest;

public class AdditionalDataOptionsMapper {

    private AdditionalDataOptionsMapper() {
    }

    public static AdditionalDataOptionsRequest toAdditionalDataOptionsRequest(AdditionalDataOptionsRequestDto dto) {
        if (dto != null) {
            return new AdditionalDataOptionsRequest.Builder()
                    .useExcludeList(dto.useExcludeList())
                    .build();
        }
        return new AdditionalDataOptionsRequest.Builder().build();
    }
}
