package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.PersonWorkTimePattern;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface PersonWorkTimePatternRepository extends CrudRepository<PersonWorkTimePattern, UUID> {
    @Query( nativeQuery = true, value = "select pwtp.* from PersonWorkTimePattern pwtp where pwtp.person in :personIds" )
    List<PersonWorkTimePattern> findByPersonIds(List<UUID> personIds);
}