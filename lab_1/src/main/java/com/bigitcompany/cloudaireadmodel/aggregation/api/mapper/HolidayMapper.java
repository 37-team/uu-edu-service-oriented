package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.DayTypeDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.HolidayDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Holiday;

import java.util.List;

public class HolidayMapper {

    private HolidayMapper() {
    }

    public static List<HolidayDto> toDtos(List<Holiday> holidays) {
        return holidays.stream().map(HolidayMapper::toDto).toList();
    }

    public static HolidayDto toDto(Holiday holiday) {
        return new HolidayDto(holiday.getDay(), DayTypeDto.valueOf(holiday.getDayType().name()));
    }
}