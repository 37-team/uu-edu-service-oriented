package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.WorkTime;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.WorkTimeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkTimeService implements RecordAccessService {

    private final WorkTimeRepository repository;

    public WorkTimeService(WorkTimeRepository repository) {
        this.repository = repository;
    }

    public Optional<WorkTime> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> records) {
        List<WorkTime> entities = records.stream().map(WorkTime::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    @Transactional
    public void save(Map<String, Object> map) {
        var workTime = new WorkTime(map);
        this.repository.save(workTime);
    }

    @Override
    @Transactional
    public void update(UUID id, Map<String, Object> map) {
        Optional<WorkTime> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent work time");
        }

        WorkTime updated = existing.get().copy(map);
        this.repository.save(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }
}