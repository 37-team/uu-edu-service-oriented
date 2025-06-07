package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkTimeBookingDto extends AbstractPage {

    List<DataDto> data;

    public WorkTimeBookingDto() {
        // For Jackson
    }

    public WorkTimeBookingDto(List<DataDto> data) {
        this.data = data;
    }

    public WorkTimeBookingDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<WorkTimeBookingDto.DataDto> data) {
        super(truncated, pageSize, currentPage, lastPage, totalObjectCount);
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public void setData(List<DataDto> data) {
        this.data = data;
    }

    public record DataDto (PersonDto person, WorkTimeDto workTime) implements Serializable {

    }

    public record PersonDto(String id) {
    }

    public record WorkTimeDto(String id, String startDateTime, String endDateTime) {
    }
}