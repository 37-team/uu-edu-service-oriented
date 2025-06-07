package com.bigitcompany.cloudaireadmodel.aggregation.domain.services;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.EntityRepository;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BusinessPartnerAggregateService {

    private final EntityRepository entityRepository;

    private final ActiveTenantProvider activeTenantProvider;

    public BusinessPartnerAggregateService(EntityRepository entityRepository, ActiveTenantProvider activeTenantProvider) {
        this.entityRepository = entityRepository;
        this.activeTenantProvider = activeTenantProvider;
    }

    @Transactional(readOnly = true)
    public Entity getBusinessPartner(UUID businessPartnerId) {
        String tenant = activeTenantProvider.resolveCurrentTenantIdentifier();
        var entity = this.entityRepository.getByObjectId(businessPartnerId, tenant, "businesspartner");

        if (entity == null) {
            throw new DomainResponseException(HttpStatus.NOT_FOUND, String.format("Business partner with provided ID %s does not exists", businessPartnerId));
        }
        return entity;
    }
}