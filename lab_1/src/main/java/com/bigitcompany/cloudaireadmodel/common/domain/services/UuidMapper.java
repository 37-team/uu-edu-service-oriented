package com.bigitcompany.cloudaireadmodel.common.domain.services;

import com.bigitcompany.cloudaireadmodel.persistence.events.mappers.StringMapper;
import org.apache.avro.generic.GenericData;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UuidMapper {

    private UuidMapper() {
        throw new IllegalStateException("Static utility class should not be instantiated");
    }

    /**
     * Converts standard UUID format to FSM formatted ID string.
     *
     * @param uuid UUID (e.g. 945045c6-9f54-41f6-839e-65a88512d836)
     * @return FSM ID (e.g. 945045C69F5441F6839E65A88512D836)
     */
    public static String toFsmId(UUID uuid) {
        return UuidMapper.toFsmId(uuid.toString());
    }

    /**
     * Converts standard UUID formatted string to standard FSM ID format.
     *
     * @param uuid UUID formatted string (e.g. 945045c6-9f54-41f6-839e-65a88512d836)
     * @return FSM ID (e.g. 945045C69F5441F6839E65A88512D836)
     */
    public static String toFsmId(String uuid) {
        return uuid.replace("-", "").toUpperCase();
    }

    public static UUID toUUID(String id) {
        return UuidMapper.toUUID(id, false);
    }

    /**
     * Converts FSM formatted ID string to standard UUID format.
     *
     * @param id FSM ID (e.g. 945045C69F5441F6839E65A88512D836)
     * @param strictMode if set to true null values throw an exception, if false results in  null result instead
     * @return UUID formatted ID (e.g. 945045c6-9f54-41f6-839e-65a88512d836)
     */
    public static UUID toUUID(String id, boolean strictMode) {
        if (id == null && strictMode) {
            throw new IllegalArgumentException("Invalid UUID provided");
        } else if (id == null) {
            return null;
        }

        try {
            if (!id.contains("-")) {
                id = id.substring(0, 8) + "-"
                        + id.substring(8, 12) + "-"
                        + id.substring(12, 16) + "-"
                        + id.substring(16, 20) + "-"
                        + id.substring(20);
            }

            // 8-4-4-4-12
            return UUID.fromString(id);
        } catch (StringIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Argument ['%s'] is not a valid UUID", id));
        }
    }

    /**
     * Converts FSM formatted ID strings to standard UUID format.
     *
     * @param uuidStrings FSM ID (e.g. 945045C69F5441F6839E65A88512D836)
     * @return UUID formatted ID (e.g. 945045c6-9f54-41f6-839e-65a88512d836)
     */
    @NotNull
    public static List<UUID> toUUIDs(List<String> uuidStrings) {
        return UuidMapper.toUUIDs(uuidStrings, false);
    }

    /**
     * Converts FSM formatted ID strings to standard UUID format.
     *
     * @param uuidAsStrings FSM ID (e.g. 945045C69F5441F6839E65A88512D836)
     * @param strictMode  if set to true at least one invalid value throws an exception, if false null values are ignored
     * @return UUID formatted ID (e.g. 945045c6-9f54-41f6-839e-65a88512d836)
     */
    @NotNull
    public static List<UUID> toUUIDs(List<String> uuidAsStrings, boolean strictMode) {
        return uuidAsStrings.stream()
                .filter(Objects::nonNull)
                .map(id -> UuidMapper.toUUID(id, strictMode))
                .toList();
    }

    public static UUID[] toUUIDs(GenericData.Array<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<UUID>().toArray(UUID[]::new);
        }
        return Arrays.stream(ids.toArray()).map(str -> UuidMapper.toUUID((String) str)).toArray(UUID[]::new);
    }

    /**
     * Converts standard UUIDs to FSM ID formatted strings.
     *
     * @param uuids UUID formatted string (e.g. 945045c6-9f54-41f6-839e-65a88512d836)
     * @return FSM ID (e.g. 945045C69F5441F6839E65A88512D836)
     */
    public static List<String> toFsmIds(List<UUID> uuids) {
        return uuids.stream().map(UuidMapper::toFsmId).toList();
    }

    public static GenericData.Array<String> toGenericDataArray(List<UUID> items) {
        List<String> strings = new ArrayList<>();

        if (items != null && !items.isEmpty()) {
            strings = items.stream().map(UuidMapper::toFsmId).toList();
        }

        return StringMapper.toGenericDataArray(strings);
    }
}
