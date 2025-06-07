package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Requirement;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RequirementRepository extends CrudRepository<Requirement, UUID> {
}
