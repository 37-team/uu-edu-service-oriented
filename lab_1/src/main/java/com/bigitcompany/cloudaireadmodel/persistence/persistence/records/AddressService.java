package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Address;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.AddressRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.NoResultException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddressService implements RecordAccessService {

    private final AddressRepository repository;

    public AddressService(AddressRepository addressRepository) {
        this.repository = addressRepository;
    }

    public Optional<Address> read(UUID id) {
        return repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> addressRecords) {
        List<Address> entities = addressRecords.stream().map(Address::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> addressRecord) {
        this.saveAll(Collections.singletonList(addressRecord));
    }

    @Override
    public void update(UUID id, Map<String, Object> addressRecord) {
        Optional<Address> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent address");
        }

        Address updated = existing.get().copy(addressRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}