package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Activity;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.ActivityRepository;

import jakarta.persistence.NoResultException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActivityService implements RecordAccessService {

    ActivityRepository repository;

    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public Optional<Activity> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> activityRecords) {
        List<Activity> entities = activityRecords.stream().map(Activity::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> activityRecord) {
        this.saveAll(Collections.singletonList(activityRecord));
    }

    @Override
    @Transactional
    public void update(UUID id, Map<String, Object> activityRecord) {
        Optional<Activity> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }

        Activity updated = existing.get().copy(activityRecord);
        this.repository.save(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

}