package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Repository
public class EntityDataRepository {

    private final UdfRepository udfRepository;

    public EntityDataRepository(UdfRepository udfRepository) {
        this.udfRepository = udfRepository;
    }

    public  Map<UUID, Map<String, String>> getEntityByEntityIds(List<UUID> entityIds, String tenant, FetchesFilter udfFilter, String tableName) {

        // ----- fetch UDFs -----
        Map<UUID, List<Udf>> entitytUdfMap = udfRepository.getUdfsByObjectIds(entityIds, tenant, udfFilter, tableName);

        // ----- combine and return -----
        Map<UUID, Map<String, String>> entityMap = new HashMap<>();
        for (UUID entityId : Objects.requireNonNullElse(entityIds, new ArrayList<UUID>())) {

            Map<String, String> entityUdfs = new HashMap<>();
            for (Udf udf : Objects.requireNonNullElse(entitytUdfMap.get(entityId), new ArrayList<Udf>())) {
                entityUdfs.put(udf.getName(), udf.getValue());
            }
            entityMap.put(entityId, entityUdfs);

        }
        return entityMap;
    }
}
