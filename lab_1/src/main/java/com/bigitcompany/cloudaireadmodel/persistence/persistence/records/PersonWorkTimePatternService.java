package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.PersonWorkTimePattern;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.PersonWorkTimePatternRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PersonWorkTimePatternService implements RecordAccessService {

    @Autowired
    PersonWorkTimePatternRepository repository;

    @Override
    public void saveAll(List<Map<String, Object>> personWtpRecords) {
        List<PersonWorkTimePattern> entities = personWtpRecords.stream().map(PersonWorkTimePattern::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> personWtpRecord) {
        var personWorkTimePattern = new PersonWorkTimePattern(personWtpRecord);
        repository.save(personWorkTimePattern);
    }

    @Override
    public void update(UUID id, Map<String, Object> personWtpRecord) {
        Optional<PersonWorkTimePattern> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent person");
        }

        PersonWorkTimePattern updated = existing.get().copy(personWtpRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<PersonWorkTimePattern> findByPersonIds(List<UUID> personIds) {
        return repository.findByPersonIds(personIds);
    }

    public Optional<PersonWorkTimePattern> read(UUID id) {
        return this.repository.findById(id);
    }
}