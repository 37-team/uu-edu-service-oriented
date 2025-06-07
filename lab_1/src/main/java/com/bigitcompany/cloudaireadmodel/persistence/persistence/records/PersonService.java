package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Person;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.PersonRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PersonService implements RecordAccessService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveAll(List<Map<String, Object>> records) {
        List<Person> entities = records.stream().map(Person::new).toList();
        repository.saveAll(entities);
    }

    public List<Person> readByRefId(List<UUID> refIds) {
        return repository.findByRefIds(refIds);
    }

    public Optional<Person> read(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void save(Map<String, Object> personRecord) {
        var person = new Person(personRecord);
        repository.save(person);
    }

    @Override
    public void update(UUID id, Map<String, Object> personRecord) {
        Optional<Person> existing = repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent person");
        }

        Person updated = existing.get().copy(personRecord);
        repository.save(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}