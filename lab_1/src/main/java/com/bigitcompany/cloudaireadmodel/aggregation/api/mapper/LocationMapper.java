package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.LocationDto;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

public class LocationMapper {

    private LocationMapper() {
    }

    public static LocationDto toDto(Location location) {
        return new LocationDto(location.getLatitude(), location.getLongitude());
    }

}