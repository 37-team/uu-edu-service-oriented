package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.PersonReservation;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.PersonReservationRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PersonReservationService implements RecordAccessService {

    private final PersonReservationRepository repository;

    public PersonReservationService(PersonReservationRepository repository) {
        this.repository = repository;
    }

    public Optional<PersonReservation> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> personReservationRecords) {
        List<PersonReservation> entities = personReservationRecords.stream().map(PersonReservation::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> personReservationRecord) {
        this.saveAll(Collections.singletonList(personReservationRecord));
    }

    @Override
    @Transactional
    public void update(UUID id, Map<String, Object> personReservationRecord) {
        Optional<PersonReservation> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent person reservation");
        }

        PersonReservation updated = existing.get().copy(personReservationRecord);
        this.repository.save(updated);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

}