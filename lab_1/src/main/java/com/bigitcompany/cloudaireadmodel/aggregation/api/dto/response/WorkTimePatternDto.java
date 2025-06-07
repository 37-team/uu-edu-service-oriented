package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public class WorkTimePatternDto {

    private final Instant start;
    private final Instant end;
    private final List<WeekPatternDto> weeks;

    @JsonCreator()
    public WorkTimePatternDto(
            @JsonProperty("start") Instant start,
            @JsonProperty("end") Instant end,
            @JsonProperty("weeks") List<WeekPatternDto> weeks) {
        this.start = start;
        this.end = end;
        this.weeks = weeks;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public List<WeekPatternDto> getWeeks() {
        return weeks;
    }
}