package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.BusinessPartner;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BusinessPartnerRepository extends CrudRepository<BusinessPartner, UUID> {
}