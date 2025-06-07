package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.BusinessPartner;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Equipment;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.LocationDto;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue;

import java.util.List;

@SuppressWarnings("java:S6204") // Returned lists need to be mutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public ActivityQueryApiDto() {
        // For Jackson
    }

    public ActivityQueryApiDto(List<DataDto> data) {
        this.data = data;
    }

    public ActivityQueryApiDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<DataDto> data) {
        super(truncated, pageSize, currentPage, lastPage, totalObjectCount);
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public void setData(List<DataDto> data) {
        this.data = data;
    }

    public static class DataDto {

        private ActivityDto activity;

        private AddressDto address;

        private ServiceCallDto serviceCall;

        private Equipment equipment;

        private BusinessPartner businessPartner;

        public DataDto(ActivityDto activity, AddressDto address, ServiceCallDto serviceCall, Equipment equipment, BusinessPartner businessPartner) {
            this.activity = activity;
            this.address = address;
            this.serviceCall = serviceCall;
            this.equipment = equipment;
            this.businessPartner = businessPartner;
        }

        public DataDto() {
        }

        public ActivityDto getActivity() {
            return activity;
        }

        public AddressDto getAddress() {
            return address;
        }

        public ServiceCallDto getServiceCall() {
            return serviceCall;
        }

        public Equipment getEquipment() {
            return equipment;
        }

        public BusinessPartner getBusinessPartner() {
            return businessPartner;
        }
    }

    public static class ActivityDto {
        private Integer travelTimeFromInMinutes;

        private String earliestStartDateTime;

        private long lastChanged;

        private Integer durationInMinutes;

        private String endDateTime;

        private String startDateTime;

        private Integer plannedDurationInMinutes;

        private String dueDateTime;

        private Integer travelTimeToInMinutes;

        private String executionStage;

        private List<String> responsibles;

        private String id;

        private String externalId;

        private List<UdfValue> udfValues;

        private String syncStatus;

        @SuppressWarnings("java:S107")
        public ActivityDto(Integer travelTimeFromInMinutes,
                           String earliestStartDateTime,
                           long lastChanged,
                           Integer durationInMinutes,
                           String endDateTime,
                           String startDateTime,
                           Integer plannedDurationInMinutes,
                           String dueDateTime,
                           Integer travelTimeToInMinutes,
                           String executionStage,
                           List<String> responsibles,
                           String id,
                           String externalId,
                           List<UdfValue> udfValues,
                           String syncStatus) {
            this.travelTimeFromInMinutes = travelTimeFromInMinutes;
            this.earliestStartDateTime = earliestStartDateTime;
            this.lastChanged = lastChanged;
            this.durationInMinutes = durationInMinutes;
            this.endDateTime = endDateTime;
            this.startDateTime = startDateTime;
            this.plannedDurationInMinutes = plannedDurationInMinutes;
            this.dueDateTime = dueDateTime;
            this.travelTimeToInMinutes = travelTimeToInMinutes;
            this.executionStage = executionStage;
            this.responsibles = responsibles;
            this.id = id;
            this.externalId = externalId;
            this.udfValues = udfValues;
            this.syncStatus = syncStatus;
        }

        public ActivityDto() {
            // For Jackson
        }

        public Integer getTravelTimeFromInMinutes() {
            return travelTimeFromInMinutes;
        }

        public String getEarliestStartDateTime() {
            return earliestStartDateTime;
        }

        public long getLastChanged() {
            return lastChanged;
        }

        public Integer getDurationInMinutes() {
            return durationInMinutes;
        }

        public String getEndDateTime() {
            return endDateTime;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public Integer getPlannedDurationInMinutes() {
            return plannedDurationInMinutes;
        }

        public String getDueDateTime() {
            return dueDateTime;
        }

        public Integer getTravelTimeToInMinutes() {
            return travelTimeToInMinutes;
        }

        public String getExecutionStage() {
            return executionStage;
        }

        public List<String> getResponsibles() {
            return responsibles;
        }

        public String getId() {
            return id;
        }

        public String getExternalId() {
            return externalId;
        }

        public List<UdfValue> getUdfValues() {
            return udfValues;
        }

        public String getSyncStatus() {
            return syncStatus;
        }
    }

    public static class AddressDto {

        private LocationDto location;

        public AddressDto(LocationDto location) {
            this.location = location;
        }

        public AddressDto() {
            // For Jackson
        }

        public LocationDto getLocation() {
            return location;
        }
    }


    public static class ServiceCallDto {

        private final String id;

        private final String externalId;

        private final List<UdfValue> udfValues;

        private final String priority;

        public ServiceCallDto(String id, String externalId, List<UdfValue> udfValues, String priority) {
            this.id = id;
            this.externalId = externalId;
            this.udfValues = udfValues;
            this.priority = priority;
        }

        public String getId() {
            return id;
        }

        public String getExternalId() {
            return externalId;
        }

        public List<UdfValue> getUdfValues() {
            return udfValues;
        }

        public String getPriority() {
            return priority;
        }
    }
}