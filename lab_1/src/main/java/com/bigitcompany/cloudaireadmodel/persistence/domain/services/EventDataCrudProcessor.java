package com.bigitcompany.cloudaireadmodel.persistence.domain.services;

import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.RecordAccessService;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Orchestrate CRUD operations on received events.
 */
public final class EventDataCrudProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EventDataCrudProcessor.class);

    private EventDataCrudProcessor() {
        // no-args constructor
    }

    public static void createAll(List<SpecificRecordBase> after, RecordAccessService service) {
        if (Objects.isNull(after)) {
            throw new NullPointerException("Null record list provided for CREATE ALL");
        }

        List<Map<String, Object>> maps = after.stream().map(MapMapper::fromRecord).toList();
        service.saveAll(maps);
    }

    public static void create(SpecificRecordBase after, RecordAccessService service) {
        if (Objects.isNull(after)) {
            throw new NullPointerException("Null record provided for CREATE");
        }

        Map<String, Object> map = MapMapper.fromRecord(after);
        service.save(map);
    }

    public static void delete(String id, RecordAccessService service) {
        var uuid = UuidMapper.toUUID(id);
        service.delete(uuid);
    }

    public static void update(
        String id,
        SpecificRecordBase before,
        SpecificRecordBase after,
        List<String> changedFields,
        RecordAccessService service
    ) {

        if (before == null && after == null) {
            throw new NullPointerException("Null before and after records provided for UPDATE");
        } else if (before == null) {
            throw new NullPointerException("Null record (before) provided for UPDATE");
        } else if (after == null) {
            throw new NullPointerException("Null record (after) provided for UPDATE");
        } else if (Objects.equals(after, before)) {
            logger.warn("Identical before and after records provided for UPDATE, skipping update");
            return;
        }

        Map<String, Object> fieldsToBeUpdated = new HashMap<>();
        String beforeSimpleClassName = before.getClass().getSimpleName();
        for (String changeField : changedFields) {
            if (before.hasField(changeField) && after.hasField(changeField)) {
                if (Objects.equals(before.get(changeField), after.get(changeField))) {
                    logger.warn("No change for {} on field {}: before and after have the same value {}", beforeSimpleClassName, changeField, after.get(changeField));
                } else {
                    fieldsToBeUpdated.put(changeField, after.get(changeField));
                }
            }
        }

        var uuid = UuidMapper.toUUID(id);
        service.update(uuid, fieldsToBeUpdated);
    }

    public static boolean isDelete(SpecificRecordBase after, List<String> changedFields) {
        if (after == null) {
            return true;
        }

        if (changedFields.contains("deleted")) {
            Object deleted = after.get("deleted");
            return (boolean) deleted; // if it is not Boolean -> fail
        }

        return false;
    }
}