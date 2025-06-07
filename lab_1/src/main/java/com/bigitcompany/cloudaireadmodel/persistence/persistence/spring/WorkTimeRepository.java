package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.WorkTime;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WorkTimeRepository extends CrudRepository<WorkTime, UUID> {
}
