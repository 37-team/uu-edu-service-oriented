package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.PersonReservation;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PersonReservationRepository extends CrudRepository<PersonReservation, UUID> {
}
