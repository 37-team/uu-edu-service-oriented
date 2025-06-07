package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.ServiceCall;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ServiceCallRepository extends CrudRepository<ServiceCall, UUID> {
}