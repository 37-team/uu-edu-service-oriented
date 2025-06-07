package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request;

import java.util.List;

public class IdsDto {

    private List<String> ids;

    public IdsDto() {
        // Jackson
    }

    public IdsDto(List<String> ids) {
        this.ids = ids;
    }

    public List<String> getIds() {
        return ids;
    }
}