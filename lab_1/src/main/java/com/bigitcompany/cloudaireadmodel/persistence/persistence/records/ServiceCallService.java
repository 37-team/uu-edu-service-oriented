package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.ServiceCall;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.ServiceCallRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceCallService implements RecordAccessService {


    private final ServiceCallRepository repository;

    public ServiceCallService(ServiceCallRepository repository) {
        this.repository = repository;
    }

    public Optional<ServiceCall> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> serviceCallRecords) {
        List<ServiceCall> entities = serviceCallRecords.stream().map(ServiceCall::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> serviceCallRecord) {
        var entity = new ServiceCall(serviceCallRecord);
        this.repository.save(entity);
    }

    @Override
    public void update(UUID id, Map<String, Object> serviceCallRecord) {
        Optional<ServiceCall> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }


        ServiceCall updated = existing.get().copy(serviceCallRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

}