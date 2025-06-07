package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Booking {
    private Instant start;

    private Instant end;

    private Location location;

    private BookingJob job;

    private boolean exclusive;

    public Booking() {
    }

    public Booking(Instant start, Instant end, Location location, BookingJob job, boolean exclusive) {
        this.start = start;
        this.end = end;
        this.location = location;
        this.job = job;
        this.exclusive = exclusive;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public Location getLocation() {
        return location;
    }

    public BookingJob getJob() {
        return job;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean hasJob() {
        return job != null;
    }

    public boolean relatesToServiceCall() {
        return job != null && job.getServiceCallId() != null;
    }

    public boolean relatesToBusinessPartner() {
        return job != null && job.getBusinessPartnerId() != null;
    }

    public boolean relatesToEquipment() {
        return job != null && job.getEquipmentId() != null;
    }

    // mutators
    public void addJobUdfs(List<Udf> udfs) {
        if (udfs != null && !udfs.isEmpty()) {
            udfs.forEach(udf -> job.addUdf(udf.getName(), udf.getValue()));
        }
    }

    public void setJobEquipment(Entity equipment) {
        if (equipment != null) {
            job.setEquipment(equipment);
        }
    }

    public void setJobBusinessPartner(Entity businessPartner) {
        if (businessPartner != null) {
            job.setBusinessPartner(businessPartner);
        }
    }

    public static final class Builder {
        private Instant start;

        private Instant end;

        private Location location;

        private BookingJob job;

        private boolean exclusive;

        public Builder start(Instant start) {
            this.start = start;
            return this;
        }

        public Builder end(Instant end) {
            this.end = end;
            return this;
        }

        public Builder location(Location location) {
            this.location = location;
            return this;
        }

        /**
         * This method sets the job for a booking and also copies the job location as booking location.
         * @param job to be set
         */
        public Builder job(BookingJob job) {
            this.job = job;
            if (job != null) {
                location = job.getLocation();
            }
            return this;
        }

        public Builder exclusive(boolean exclusive) {
            this.exclusive = exclusive;
            return this;
        }

        public Booking build() {
            return new Booking(start, end, location, job, exclusive);
        }
    }

    @Override
    public String toString() {
        return "Booking{" +
            "start=" + start +
            ", end=" + end +
            ", location=" + location +
            ", job=" + job +
            ", exclusive=" + exclusive +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var booking = (Booking) o;
        return isExclusive() == booking.isExclusive() && Objects.equals(getStart(), booking.getStart()) && Objects.equals(getEnd(), booking.getEnd()) && Objects.equals(getLocation(), booking.getLocation()) && Objects.equals(getJob(), booking.getJob());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStart(), getEnd(), getLocation(), getJob(), isExclusive());
    }
}