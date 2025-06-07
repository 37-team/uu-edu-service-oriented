package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceIdQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public ResourceIdQueryApiDto() {
        // For Jackson
    }

    public ResourceIdQueryApiDto(List<DataDto> data) {
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public static class DataDto {

        private Person person;

        public DataDto() {
            // For Jackson
        }

        public DataDto(Person person) {
            this.person = person;
        }

        public Person getPerson() {
            return person;
        }
    }

    public record Person(String id) implements Serializable {
    }
}
