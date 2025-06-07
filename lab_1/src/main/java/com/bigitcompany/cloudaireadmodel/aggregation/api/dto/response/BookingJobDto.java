package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

public class BookingJobDto {

    private String id;

    private Instant lastChanged;

    private LocationDto location;

    private EquipmentDto equipment;

    private BusinessPartnerDto businessPartner;

    @Schema(
            type="object",
            example = """
                    {
                      "urgency": "1"
                    }"""
    )
    private Map<String, String> udfValues;

    public BookingJobDto() {
    }

    public BookingJobDto(String id, Instant lastChanged, LocationDto location, EquipmentDto equipment, BusinessPartnerDto businessPartner, Map<String, String> udfValues) {
        this.id = id;
        this.lastChanged = lastChanged;
        this.location = location;
        this.udfValues = udfValues;
        this.equipment = equipment;
        this.businessPartner = businessPartner;
    }

    public String getId() {
        return id;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public LocationDto getLocation() {
        return location;
    }

    public EquipmentDto getEquipment() {
        return equipment;
    }

    public BusinessPartnerDto getBusinessPartner() {
        return businessPartner;
    }

    public Map<String, String> getUdfValues() {
        return udfValues;
    }
}