package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.equipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Equipment;

import java.util.List;

@SuppressWarnings("java:S6204") // Returned lists need to be mutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class EquipmentQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public EquipmentQueryApiDto() {
        // For Jackson
    }

    public EquipmentQueryApiDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<EquipmentQueryApiDto.DataDto> data) {
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

        private Equipment equipment;

        public DataDto() {
            // For Jackson
        }

        public DataDto(Equipment equipment) {
            this.equipment = equipment;
        }

        public Equipment getEquipment() {
            return equipment;
        }
    }
}