package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.job;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityAndTagDto extends AbstractPage {

    private List<DataDto> data;

    public ActivityAndTagDto() {
        // For Jackson
    }

    public ActivityAndTagDto(List<DataDto> data) {
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public void setData(List<DataDto> data) {
        this.data = data;
    }

    public static class DataDto {
        private Activity activity;

        private Tag tag;

        public DataDto() {
            // For Jackson
        }

        public DataDto(Activity activity, Tag tag) {
            this.activity = activity;
            this.tag = tag;
        }

        public Activity getActivity() {
            return activity;
        }

        public Tag getTag() {
            return tag;
        }
    }

    public record Activity(String id) implements Serializable {
    }

    public record Tag(String name) implements Serializable {
    }
}
