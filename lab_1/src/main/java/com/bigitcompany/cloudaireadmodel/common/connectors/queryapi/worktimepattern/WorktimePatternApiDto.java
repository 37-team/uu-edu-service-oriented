package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.worktimepattern;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;

import java.io.Serializable;
import java.util.List;

/**
 * A class like SkillQueryApiDto, but with the following objects in the DataDto:
 * Person, with Id
 * PersonWorktimePattern, with Id, start and end (all strings)
 * WorktimePattern, with Id, Weeks (object), with days (object), with hours (object), with start and end (all strings)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorktimePatternApiDto extends AbstractPage {

    private List<DataDto> data;

    public WorktimePatternApiDto() {
        // For Jackson
    }

    public WorktimePatternApiDto(List<DataDto> data) {
        this.data = data;
    }

    public WorktimePatternApiDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<WorktimePatternApiDto.DataDto> data) {
        super(truncated, pageSize, currentPage, lastPage, totalObjectCount);
        this.data = data;
    }

    public WorktimePatternApiDto setData(List<DataDto> data) {
        this.data = data;
        return this;
    }

    public List<DataDto> getData() {
        return data;
    }


    public record DataDto(
            WorktimePatternApiDto.Person person,
            WorktimePatternApiDto.PersonWorktimePattern personWorkTimePattern,
            WorktimePatternApiDto.WorktimePattern workTimePattern
    ) { }

    public record Person(String id) implements Serializable { }

    public record PersonWorktimePattern(String id, String startDate, String endDate) implements Serializable { }

    public record WorktimePattern(String id, List<Week> weeks) implements Serializable { }

    public record Week(List<DayPattern> days) implements Serializable { }

    public record DayPattern(List<TimeRange> times) implements Serializable { }

    public record TimeRange(String startTime, String endTime) implements Serializable { }
}
