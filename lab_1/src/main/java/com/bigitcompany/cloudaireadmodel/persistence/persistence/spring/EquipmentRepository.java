package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Equipment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EquipmentRepository extends CrudRepository<Equipment, UUID> {

}
