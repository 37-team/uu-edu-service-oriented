package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.FetchRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.FetchResourceRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchRequest;

public class FetchMapper {

    private FetchMapper() {
        // private constructor to prevent instantiating a class with only static methods
    }
    public static FetchRequest toFetchUdfRequest(FetchRequestDto dto) {
        if (dto == null || dto.getUdfs() == null) {
            return new FetchRequest.Builder().build();
        }
        return new FetchRequest.Builder()
                .requestData(dto.getUdfs())
                .doNotReturn(dto.getUdfs())
                .build();
    }

    public static FetchRequest toFetchSkillRequest(FetchResourceRequestDto dto) {
        if (dto == null || dto.getSkills() == null) {
            return new FetchRequest.Builder().build();
        }
        return new FetchRequest.Builder()
            .requestData(dto.getSkills())
            .doNotReturn(dto.getSkills())
            .build();
    }
}
