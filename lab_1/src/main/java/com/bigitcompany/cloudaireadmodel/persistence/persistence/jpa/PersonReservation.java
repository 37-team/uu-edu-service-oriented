package com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa;

import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.MapUtility;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import io.hypersistence.utils.hibernate.type.array.UUIDArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "personReservation")
public class PersonReservation {

    private static final String PERSONS_FIELD = "persons";

    @Id
    @Column(name = "id", unique = true)
    private UUID id;

    @Column(name = "persons", columnDefinition = "uuid[]", nullable = false)
    @Type(UUIDArrayType.class)
    private UUID[] persons;

    @Column(name = "startDate")
    private Instant startDate;

    @Column(name = "endDate")
    private Instant endDate;

    @Column(name = "exclusive")
    private Boolean exclusive;

    @Column(name = "lastChanged")
    private Instant lastChanged;

    @Column(name = "address")
    private UUID address;

    public PersonReservation() {
    }

    public PersonReservation(Map<String, Object> records) {
        id = UuidMapper.toUUID(((String) records.get("id")));

        if (records.containsKey(PERSONS_FIELD) && records.get(PERSONS_FIELD) != null) {
            Object potentialPersons = records.get(PERSONS_FIELD);
            List<String> confirmedPersons = new ArrayList<>();
            if (potentialPersons instanceof List<?> potentialPersonsList) {
                for (Object potentialPerson : potentialPersonsList) {
                    if (potentialPerson instanceof String potentialPersonString) {
                        confirmedPersons.add(potentialPersonString);
                    }
                }
            }
            persons = UuidMapper.toUUIDs(confirmedPersons).toArray(UUID[]::new);
        }

        startDate = DateTimeService.toInstant((String) records.get("startDate"));
        endDate = DateTimeService.toInstant((String) records.get("endDate"));
        lastChanged = DateTimeService.toInstant((String) records.get("lastChanged"));

        exclusive = (Boolean) records.get("exclusive");
        address = UuidMapper.toUUID((String) records.get("address"));
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("id", UuidMapper.toFsmId(id));
        values.put(PERSONS_FIELD, getPersons().stream().map(UUID::toString).toList());
        values.put("startDate", startDate.toString());
        values.put("endDate", endDate.toString());
        values.put("lastChanged", lastChanged.toString());
        values.put("exclusive", exclusive);
        values.put("address", address.toString());
        return values;
    }

    public PersonReservation copy(Map<String, Object> overrides) {
        Map<String, Object> merged = MapUtility.merge(toMap(), overrides);
        return new PersonReservation(merged);
    }

    public UUID getId() {
        return id;
    }

    public List<UUID> getPersons() {
        return List.of(persons);
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Boolean getExclusive() {
        return exclusive;
    }

    public Instant getLastChanged() {
        return lastChanged;
    }

    public UUID getAddress() {
        return address;
    }
}