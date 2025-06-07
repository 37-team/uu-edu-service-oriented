package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.exception.InconsistentDataException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Resource {

    private UUID id;

    private Instant lastChanged;

    private ResourceType origin;

    private Location realTimeLocation;

    private Instant locationLastUserChangedDate;

    private Location homeLocation;

    private List<Skill> skills;

    private List<Booking> bookings;

    private List<WorkTimePattern> workTimePattern;

    private String externalId;

    private Map<String, String> udfValues;

    private List<Holiday> holidays;

    public Resource() {
    }

    public Resource(UUID id, Instant lastChanged, ResourceType origin, Location realTimeLocation, Instant locationLastUserChangedDate, Location homeLocation, List<Skill> skills, List<Booking> bookings, List<WorkTimePattern> workTimePattern, String externalId, Map<String, String> udfValues, List<Holiday> holidays) {
        this.id = id;
        this.lastChanged = lastChanged;
        this.origin = origin;
        this.realTimeLocation = realTimeLocation;
        this.locationLastUserChangedDate = locationLastUserChangedDate;
        this.homeLocation = homeLocation;
        this.skills = skills;
        this.bookings = bookings;
        this.workTimePattern = workTimePattern;
        this.externalId = externalId;
        this.udfValues = udfValues;
        this.holidays = holidays;
    }

    public UUID getId() {
        return id;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public ResourceType getResourceType() {
        return origin;
    }

    public Location getRealTimeLocation() {
        return realTimeLocation;
    }

    public Instant getLocationLastUserChangedDate() {
        return locationLastUserChangedDate;
    }

    public Location getHomeLocation() {
        return homeLocation;
    }

    /** Note: returns a read-only collection, to update state a mutator method should be used. */
    public List<Skill> getSkills() {
        return Collections.unmodifiableList(skills);
    }

    /** Note: returns a read-only collection, to update state a mutator method should be used. */
    public List<Booking> getBookings() {
        return bookings;
    }

    /** Note: returns a read-only collection, to update state a mutator method should be used. */
    public List<WorkTimePattern> getWorkTimePattern() {
        return Collections.unmodifiableList(workTimePattern);
    }

    public String getExternalId() {
        return externalId;
    }

    /** Note: returns a read-only collection, to update state a mutator method should be used. */
    public Map<String, String> getUdfValues() {
        return Collections.unmodifiableMap(udfValues);
    }

    /** Note: returns a read-only collection, to update state a mutator method should be used. */
    public List<Holiday> getHolidays() {
        return Collections.unmodifiableList(holidays);
    }


    public void addUdfs(List<Udf> udfs) {
        if (udfs != null && !udfs.isEmpty()) {
            udfs.forEach(udf -> this.udfValues.put(udf.getName(), udf.getValue()));
        }
    }

    public void addSkills(List<Skill> skills) {
        if (skills != null && !skills.isEmpty()) {
            this.skills.addAll(skills);
        }
    }

    public void addBookings(List<Booking> bookings) {
        if (bookings != null && !bookings.isEmpty()) {
            this.bookings.addAll(bookings);
        }
    }

    public void addHolidays(List<Holiday> holidays) {
        if (holidays != null && !holidays.isEmpty()) {
            this.holidays.addAll(holidays);
        }
    }

    public void addHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    public static class Builder {

        private UUID id;

        private Instant lastChanged;

        private ResourceType origin;

        private Location realTimeLocation;

        private Instant locationLastUserChangedDate;

        private Location homeLocation;

        private List<Skill> skills;
        private List<Booking> bookings;
        private List<WorkTimePattern> workTimePattern;
        private String externalId;
        private Map<String, String> udfValues;
        private List<Holiday> holidays;

        public Builder() {
            this.bookings = new ArrayList<>();
            this.skills = new ArrayList<>();
            this.holidays = new ArrayList<>();
            this.udfValues = new HashMap<>();
            this.workTimePattern = new ArrayList<>();
        }

        public Builder id(UUID id) {
            if (id == null) {
                throw new InconsistentDataException("Resource must have ID");
            }
            this.id = id;
            return this;
        }

        public Builder lastChanged(Date lastChanged) {
            if(lastChanged == null) {
                throw new InconsistentDataException("Resource must have lastChanged value");
            }
            this.lastChanged = lastChanged.toInstant();
            return this;
        }

        public Builder locationLastUserChangedDate(Date locationLastUserChangedDate) {
            if (locationLastUserChangedDate != null) {
                this.locationLastUserChangedDate = locationLastUserChangedDate.toInstant();
            }
            return this;
        }

        public Builder realTimeLocation(Location realTimeLocation) {
            if (realTimeLocation != null) {
                this.realTimeLocation = realTimeLocation;
            }
            return this;
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder origin(String crowdType) {
            if(crowdType == null || crowdType.equals("NON_CROWD")) {
                this.origin = ResourceType.MAIN_CONTRACTOR;
            } else {
                this.origin = ResourceType.SERVICE_PARTNER;
            }
            return this;
        }

        public  Builder worktimePattern(List<WorkTimePattern> workTimePatterns) {
            this.workTimePattern = workTimePatterns;
            return  this;
        }

        public  Builder skills(List<Skill> skills) {
            this.skills = skills;
            return  this;
        }

        public  Builder udfValues(Map<String, String> udfValues) {
            this.udfValues = udfValues;
            return  this;
        }

        public Resource build() {
            return new Resource(
                this.id,
                this.lastChanged,
                this.origin,
                this.realTimeLocation,
                this.locationLastUserChangedDate,
                this.homeLocation,
                this.skills,
                this.bookings,
                this.workTimePattern,
                this.externalId,
                this.udfValues,
                this.holidays
            );
        }
    }
}