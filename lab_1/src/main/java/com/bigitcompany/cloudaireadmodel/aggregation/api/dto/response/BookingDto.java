package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import java.time.Instant;

public class BookingDto {

    private Instant start;

    private Instant end;

    private LocationDto location;

    private BookingJobDto job;

    private boolean exclusive;

    public BookingDto() {
    }

    public BookingDto(Instant start, Instant end, LocationDto location, BookingJobDto job, boolean exclusive) {
        this.start = start;
        this.end = end;
        this.location = location;
        this.job = job;
        this.exclusive = exclusive;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public LocationDto getLocation() {
        return location;
    }

    public BookingJobDto getJob() {
        return job;
    }

    public boolean isExclusive() {
        return exclusive;
    }
}