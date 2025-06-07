package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.BookingJobDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingJob;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class BookingJobMapper {

    private BookingJobMapper() {
    }

    public static BookingJobDto toDto(BookingJob job) {
        return new BookingJobDto(
            UuidMapper.toFsmId(job.getId()),
            job.getLastChanged(),
            job.getLocation() != null ? LocationMapper.toDto(job.getLocation()) : null,
            job.getEquipment() != null ? EquipmentMapper.toDto(job.getEquipment()) : null,
            job.getBusinessPartner() != null ? BusinessPartnerMapper.toDto(job.getBusinessPartner()) : null,
            job.getUdfValues()
        );
    }
}