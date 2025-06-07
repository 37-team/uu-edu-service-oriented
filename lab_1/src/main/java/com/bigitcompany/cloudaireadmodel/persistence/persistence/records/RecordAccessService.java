package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RecordAccessService {

    void saveAll(List<Map<String, Object>> records);

    void save(Map<String, Object> map);

    void update(UUID id, Map<String, Object> map);

    void delete(UUID id);
}