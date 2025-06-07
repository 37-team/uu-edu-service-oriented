package com.bigitcompany.cloudaireadmodel.common.domain.model;

import com.google.common.base.Objects;
import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;

import java.util.StringJoiner;

public class Location {
    private double latitude;
    private double longitude;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(com.sap.fsm.data.event.common.Location location) {
        if(location == null) {
             throw new DomainException("Location cannot be built from null location");
        }
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var location = (Location) o;
        return Double.compare(location.getLatitude(), getLatitude()) == 0 && Double.compare(location.getLongitude(), getLongitude()) == 0;
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hashCode(getLatitude(), getLongitude());
    }
    
    @Generated
    @Override
    public String toString() {
        return new StringJoiner(", ", Location.class.getSimpleName() + "[", "]")
                .add("latitude=" + latitude)
                .add("longitude=" + longitude)
                .toString();
    }
}