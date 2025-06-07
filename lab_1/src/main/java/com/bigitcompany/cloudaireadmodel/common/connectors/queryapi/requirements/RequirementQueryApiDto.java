package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.requirements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Tag;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("java:S6204") // Returned lists need to be mutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequirementQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public RequirementQueryApiDto() {
        // For Jackson
    }

    public RequirementQueryApiDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<DataDto> data) {
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

        private RequirementDto requirement;

        private Tag tag;

        public DataDto() {
            // For Jackson
        }

        public DataDto(ActivityDto activity, RequirementDto requirement, Tag tag) {
            this.activity = activity;
            this.requirement = requirement;
            this.tag = tag;
        }

        public ActivityDto getActivity() {
            return activity;
        }

        public RequirementDto getRequirement() {
            return requirement;
        }

        public Tag getTag() {
            return tag;
        }
    }

    public static class ActivityDto implements Serializable {

        private String id;

        public ActivityDto() {
        }

        public ActivityDto(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class RequirementDto implements Serializable {
        private final boolean mandatory;

        private final String tag;

        public RequirementDto(boolean mandatory, String tag) {
            this.mandatory = mandatory;
            this.tag = tag;
        }

        public boolean isMandatory() {
            return mandatory;
        }

        public String getTag() {
            return tag;
        }
    }
}