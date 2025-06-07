package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Requirement;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.RequirementRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RequirementService implements RecordAccessService {
    
    private final RequirementRepository repository;

    public RequirementService(RequirementRepository repository) {
        this.repository = repository;
    }

    public Optional<Requirement> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void save(Map<String, Object> requirementRecord) {
        this.saveAll(Collections.singletonList(requirementRecord));
    }

    @Override
    public void saveAll(List<Map<String, Object>> requirementRecords) {
        List<Requirement> requirements = requirementRecords.stream().map(Requirement::new).toList();
        this.repository.saveAll(requirements);
    }

    @Override
    public void update(UUID id, Map<String, Object> requirementRecord) {
        Optional<Requirement> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }

        Requirement updated = existing.get().copy(requirementRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }
}