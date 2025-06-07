package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

public class LocationDto {

    private final double latitude;

    private final double longitude;

    public LocationDto(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}