package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ResourceResponseDto {

    private String id;

    private Instant lastChanged;

    private ResourceTypeDto origin;

    private LocationDto realTimeLocation;

    private Instant locationLastUserChangedDate;

    private LocationDto homeLocation;

    private List<SkillDto> skills;

    private List<BookingDto> bookings;

    private List<WorkTimePatternDto> workTimePattern;

    private String externalId;

    @Schema(
        type = "object",
        example = """
            {
              "preferredTimes": "09:00-12:00" 
            }"""
    )
    private Map<String, String> udfValues;

    private List<HolidayDto> holidays;

    @JsonCreator
    public ResourceResponseDto(
        String id,
        Instant lastChanged,
        ResourceTypeDto origin,
        LocationDto realTimeLocation,
        Instant locationLastUserChangedDate,
        LocationDto homeLocation,
        List<SkillDto> skills,
        List<BookingDto> bookings,
        List<WorkTimePatternDto> workTimePattern,
        String externalId,
        Map<String, String> udfValues,
        List<HolidayDto> holidays
    ) {
        this.id = id;
        this.lastChanged = lastChanged;
        this.origin = origin;
        this.realTimeLocation = realTimeLocation;
        this.locationLastUserChangedDate = locationLastUserChangedDate;
        this.homeLocation = homeLocation;
        this.skills = skills;
        this.bookings = bookings;
        this.workTimePattern = workTimePattern;
        this.externalId = externalId;
        this.udfValues = udfValues;
        this.holidays = holidays;
    }

    public ResourceResponseDto() {
    }

    public String getId() {
        return id;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public ResourceTypeDto getOrigin() {
        return origin;
    }

    public LocationDto getRealTimeLocation() {
        return realTimeLocation;
    }

    public Instant getLocationLastUserChangedDate() {
        return locationLastUserChangedDate;
    }

    public LocationDto getHomeLocation() {
        return homeLocation;
    }

    public List<SkillDto> getSkills() {
        return skills;
    }

    public List<BookingDto> getBookings() {
        return bookings;
    }

    public List<WorkTimePatternDto> getWorkTimePattern() {
        return workTimePattern;
    }

    public String getExternalId() {
        return externalId;
    }

    public Map<String, String> getUdfValues() {
        return udfValues;
    }

    public List<HolidayDto> getHolidays() {
        return holidays;
    }
}
