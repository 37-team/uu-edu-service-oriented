package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.skill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.AbstractPage;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.Tag;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("java:S6204") // Returned lists need to be mutable
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkillQueryApiDto extends AbstractPage {

    private List<DataDto> data;

    public SkillQueryApiDto() {
        // For Jackson
    }

    public SkillQueryApiDto(List<DataDto> data) {
        this.data = data;
    }

    public List<DataDto> getData() {
        return data;
    }

    public static class DataDto {

        private Skill skill;

        private Tag tag;

        public DataDto() {
            // For Jackson
        }

        public DataDto(Skill skill, Tag tag) {
            this.skill = skill;
            this.tag = tag;
        }

        public Skill getSkill() {
            return skill;
        }

        public void setSkill(Skill skill) {
            this.skill = skill;
        }

        public Tag getTag() {
            return tag;
        }

        public void setTag(Tag tag) {
            this.tag = tag;
        }
    }

    public static class Skill implements Serializable {

        private String endDate;

        private String person;

        private String lastChanged;

        private List<String> days;

        private String startTime;

        private String id;

        private String endTime;

        private String startDate;

        public Skill() {
            // For Jackson
        }

        @SuppressWarnings("java:S107")
        public Skill(String id, String person, String startDate, String endDate, String startTime, String endTime, List<String> days, String lastChanged) {
            this.id = id;
            this.person = person;
            this.startDate = startDate;
            this.endDate = endDate;
            this.startTime = startTime;
            this.endTime = endTime;
            this.days = days;
            this.lastChanged = lastChanged;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getLastChanged() {
            return lastChanged;
        }

        public void setLastChanged(String lastChanged) {
            this.lastChanged = lastChanged;
        }

        public List<String> getDays() {
            return days;
        }

        public void setDays(List<String> days) {
            this.days = days;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }
    }
}
