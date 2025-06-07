package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.BusinessPartnerDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class BusinessPartnerMapper {

    private BusinessPartnerMapper() {
    }

    public static BusinessPartnerDto toDto(Entity businessPartner) {
        if (businessPartner == null || businessPartner.getId() == null) {
            return null;
        }
        return new BusinessPartnerDto(
            UuidMapper.toFsmId(businessPartner.getId()),
            businessPartner.getExternalId(),
            businessPartner.getUdfValues()
        );
    }
}