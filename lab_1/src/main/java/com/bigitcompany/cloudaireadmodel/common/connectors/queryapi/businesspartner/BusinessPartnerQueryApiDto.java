package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.businesspartner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.BusinessPartner;

import java.util.List;

@SuppressWarnings("java:S6204") // Returned lists need to be mutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessPartnerQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public BusinessPartnerQueryApiDto() {
        // For Jackson
    }

    public BusinessPartnerQueryApiDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<BusinessPartnerQueryApiDto.DataDto> data) {
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

        private BusinessPartner businessPartner;

        public DataDto() {
            // For Jackson
        }

        public DataDto(BusinessPartner businessPartner) {
            this.businessPartner = businessPartner;
        }

        public BusinessPartner getBusinessPartner() {
            return businessPartner;
        }
    }
}