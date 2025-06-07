package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.persistence.database.DataOperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetadataService {

    private final DataOperationsRepository dataOperationsRepository;

    @Autowired
    public MetadataService(DataOperationsRepository dataOperationsRepository) {
        this.dataOperationsRepository = dataOperationsRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> getObjectCount(Tenant tenant) {
        var map = new HashMap<String, Integer>();
        Arrays.stream(SupportedFsmDtos.values()).forEach(dto -> {
            var count = dataOperationsRepository.getObjectCount(tenant, dto.getName());
            map.put(dto.getName(), count);
        });
        return map;
    }
}
