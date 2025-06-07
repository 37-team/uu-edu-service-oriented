package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.LocationDto;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonReservationsBookingDto extends AbstractPage {

    List<DataDto> data;

    public PersonReservationsBookingDto() {
        // For Jackson
    }

    public PersonReservationsBookingDto(List<DataDto> data) {
        this.data = data;
    }

    public PersonReservationsBookingDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<PersonReservationsBookingDto.DataDto> data) {
        super(truncated, pageSize, currentPage, lastPage, totalObjectCount);
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public void setData(List<DataDto> data) {
        this.data = data;
    }

    public record DataDto (PersonDto person, PersonReservationDto personReservation, AddressDto address) implements Serializable {

    }

    public record PersonDto(String id) {
    }

    public record PersonReservationDto(String id, String startDate, String endDate, boolean exclusive) {
    }

    public record AddressDto(LocationDto location) {
    }
}