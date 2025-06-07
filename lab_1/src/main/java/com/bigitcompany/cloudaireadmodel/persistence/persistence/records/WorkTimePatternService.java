package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.WorkTimePattern;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.WorkTimePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkTimePatternService implements RecordAccessService {

    @Autowired
    WorkTimePatternRepository repository;

    @Override
    public void saveAll(List<Map<String, Object>> wtpRecords) {
        List<WorkTimePattern> entities = wtpRecords.stream().map(WorkTimePattern::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> wtpRecord) {
        var workTimePattern = new WorkTimePattern(wtpRecord);
        repository.save(workTimePattern);
    }

    @Override
    public void update(UUID id, Map<String, Object> wtpRecord) {
        Optional<WorkTimePattern> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }

        WorkTimePattern updated = existing.get().copy(wtpRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}