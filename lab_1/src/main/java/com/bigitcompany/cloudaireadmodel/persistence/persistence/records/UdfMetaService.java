package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.UdfMeta;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.UdfMetaRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UdfMetaService implements RecordAccessService {

    UdfMetaRepository repository;

    public UdfMetaService(UdfMetaRepository repository) {
        this.repository = repository;
    }


    public Optional<UdfMeta> read(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> udfMetaRecords) {
        List<UdfMeta> entities = udfMetaRecords.stream().map(UdfMeta::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> udfMetaRecord) {
        this.saveAll(Collections.singletonList(udfMetaRecord));
    }

    @Override
    public void update(UUID id, Map<String, Object> udfMetaRecord) {
        Optional<UdfMeta> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent udf meta");
        }

        UdfMeta updated = existing.get().copy(udfMetaRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    /** Note: should be used with caution */
    public void truncate() {
        repository.deleteAll();
    }
}