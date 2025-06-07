package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.BusinessPartner;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Equipment;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.LocationDto;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityAsBookingDto extends AbstractPage {

    List<DataDto> data;

    public ActivityAsBookingDto() {
        // For Jackson
    }

    public ActivityAsBookingDto(List<DataDto> data) {
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public void setData(List<DataDto> data) {
        this.data = data;
    }

    public static class DataDto {

        private PersonDto person;

        private ActivityDto activity;

        private AddressDto address;

        private ServiceCallDto serviceCall;

        private Equipment equipment;

        private BusinessPartner businessPartner;

        public DataDto() {
            // For Jackson
        }

        public DataDto(PersonDto person, ActivityDto activity, AddressDto address, ServiceCallDto serviceCall, Equipment equipment, BusinessPartner businessPartner) {
            this.person = person;
            this.activity = activity;
            this.address = address;
            this.serviceCall = serviceCall;
            this.equipment = equipment;
            this.businessPartner = businessPartner;
        }

        public PersonDto getPerson() {
            return person;
        }

        public void setPerson(PersonDto person) {
            this.person = person;
        }

        public ActivityDto getActivity() {
            return activity;
        }

        public void setActivity(ActivityDto activity) {
            this.activity = activity;
        }

        public AddressDto getAddress() {
            return address;
        }

        public void setAddress(AddressDto address) {
            this.address = address;
        }

        public ServiceCallDto getServiceCall() {
            return serviceCall;
        }

        public void setServiceCall(ServiceCallDto serviceCall) {
            this.serviceCall = serviceCall;
        }

        public Equipment getEquipment() {
            return equipment;
        }

        public void setEquipment(Equipment equipment) {
            this.equipment = equipment;
        }

        public BusinessPartner getBusinessPartner() {
            return businessPartner;
        }

        public void setBusinessPartner(BusinessPartner businessPartner) {
            this.businessPartner = businessPartner;
        }
    }

    public record PersonDto(String id) {
    }

    public record ActivityDto(String id, Long lastChanged, String startDateTime, String endDateTime,
                              String executionStage, List<UdfValue> udfValues) {
    }

    public record AddressDto(LocationDto location) {
    }

    public record ServiceCallDto(String id, List<UdfValue> udfValues) {
    }
}
