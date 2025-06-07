package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Equipment;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.EquipmentRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EquipmentService implements RecordAccessService {

    private final EquipmentRepository repository;

    public EquipmentService(EquipmentRepository repository) {
        this.repository = repository;
    }

    public Optional<Equipment> read(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> equipmentRecords) {
        List<Equipment> entities = equipmentRecords.stream().map(Equipment::new).toList();
        this.repository.saveAll(entities);    }

    @Override
    public void save(Map<String, Object> equipmentRecord) {
        this.saveAll(Collections.singletonList(equipmentRecord));
    }

    @Override
    public void update(UUID id, Map<String, Object> equipmentRecord) {
        Optional<Equipment> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent equipment");
        }

        Equipment updated = existing.get().copy(equipmentRecord);
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