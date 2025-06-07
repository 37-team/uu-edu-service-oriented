package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.LocationDto;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public PersonQueryApiDto() {
        // For Jackson
    }

    public PersonQueryApiDto(List<DataDto> data) {
        this.data = data;
    }

    public PersonQueryApiDto(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount, List<PersonQueryApiDto.DataDto> data) {
        super(truncated, pageSize, currentPage, lastPage, totalObjectCount);
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

    public record Person(String id,
                         String externalId,
                         Integer maxDistanceRadius,
                         LocationDto location,
                         String locationLastUserChangedDate,
                         String crowdType,
                         List<UdfValue> udfValues) implements Serializable {
    }
}