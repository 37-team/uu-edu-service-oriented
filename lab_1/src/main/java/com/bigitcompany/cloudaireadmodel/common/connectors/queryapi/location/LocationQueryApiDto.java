package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.location;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.LocationDto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("java:S6204") // Returned lists need to be mutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public LocationQueryApiDto() {
        // For Jackson
    }

    public LocationQueryApiDto(List<DataDto> data) {
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public record DataDto (PersonDto person,
                           AddressDto employeeAddressHomeDefault,
                           AddressDto employeeAddressWorkDefault,
                           AddressDto erpUserAddressHomeDefault,
                           AddressDto erpUserAddressWorkDefault,
                           AddressDto employeeAddressHome,
                           AddressDto employeeAddressWork,
                           AddressDto erpUserAddressHome,
                           AddressDto erpUserAddressWork
    ) implements Serializable {

    }

    public record PersonDto(String id) {
    }

    public record AddressDto(LocationDto location, String type) {
    }
}