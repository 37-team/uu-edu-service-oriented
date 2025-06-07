package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PartitionsQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public PartitionsQueryApiDto() {
        // For Jackson
    }

    public PartitionsQueryApiDto(List<DataDto> data) {
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public static class DataDto {

        private Person person;

        private Tag tag;

        public DataDto() {
            // For Jackson
        }

        public DataDto(Tag tag, Person person) {
            this.person = person;
            this.tag = tag;
        }

        public Person getPerson() {
            return person;
        }

        public Tag getTag() {
            return tag;
        }
    }

    public record Person(String id) implements Serializable { }

    public record Tag(String name) implements Serializable { }

}
