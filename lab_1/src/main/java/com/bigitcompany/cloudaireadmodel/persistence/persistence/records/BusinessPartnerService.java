package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.BusinessPartner;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.BusinessPartnerRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BusinessPartnerService implements RecordAccessService {


    private final BusinessPartnerRepository repository;

    public BusinessPartnerService(BusinessPartnerRepository repository) {
        this.repository = repository;
    }

    public Optional<BusinessPartner> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> businessPartnerRecords) {
        List<BusinessPartner> entities = businessPartnerRecords.stream().map(BusinessPartner::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> businessPartnerRecord) {
        var entity = new BusinessPartner(businessPartnerRecord);
        this.repository.save(entity);
    }

    @Override
    public void update(UUID id, Map<String, Object> businessPartnerRecord) {
        Optional<BusinessPartner> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }


        BusinessPartner updated = existing.get().copy(businessPartnerRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

}