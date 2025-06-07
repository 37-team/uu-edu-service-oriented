package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;


import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.WorkTimePattern;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkTimePatternRepository extends CrudRepository<WorkTimePattern, UUID>{

}
