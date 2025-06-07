package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.BookingsFilterDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourceOptionsDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ResourceResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ResourceTypeDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.BookingsFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Resource;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ResourceOptions;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class ResourceMapper {

    private ResourceMapper() {
    }

    public static ResourceResponseDto toValidResourcesDto(Resource resource) {
        return new ResourceResponseDto(
            UuidMapper.toFsmId(resource.getId()),
            resource.getLastChanged(),
            ResourceTypeDto.valueOf(resource.getResourceType().name()),
            resource.getRealTimeLocation() != null ? LocationMapper.toDto(resource.getRealTimeLocation()) : null,
            resource.getLocationLastUserChangedDate(),
            resource.getHomeLocation() != null ? LocationMapper.toDto(resource.getHomeLocation()) : null,
            SkillMapper.toDtos(resource.getSkills()),
            BookingMapper.toDtos(resource.getBookings()),
            WorkTimePatternMapper.toDtos(resource.getWorkTimePattern()),
            resource.getExternalId(),
            resource.getUdfValues(),
            HolidayMapper.toDtos(resource.getHolidays())
        );
    }

    public static ResourceOptions toResourceOptions(ResourceOptionsDto dto) {
        return new ResourceOptions.Builder()
            .geocodedOnly(dto.isGeocodedOnly())
            .includeInternalPersons(dto.isIncludeInternalPersons())
            .includeCrowdPersons(dto.isIncludeCrowdPersons())
            .holidaysEnabled(dto.isHolidaysEnabled())
            .cacheHolidays(dto.isCacheHolidays())
            .build();
    }


    public static BookingsFilter toBookingsFilter(BookingsFilterDto dto) {
        return new BookingsFilter.Builder(dto.getEarliest(), dto.getLatest())
            .activitiesToExclude(dto.getActivitiesToExclude())
            .considerReleasedAsExclusive(dto.isConsiderReleasedAsExclusive())
            .considerPlannedAsExclusive(dto.isConsiderPlannedAsExclusive())
            .build();
    }
}
