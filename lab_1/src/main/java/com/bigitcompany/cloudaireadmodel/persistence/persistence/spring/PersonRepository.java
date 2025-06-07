package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends CrudRepository<Person, UUID> {
    @Query( nativeQuery = true, value = "SELECT p.* FROM Person p WHERE p.id in :ids AND p.plannableresource='t' AND p.inactive='f' " )
    List<Person> findPlannableAndActive(List<UUID> ids);

    @Query( nativeQuery = true, value = "select p.* from Person p where p.refid in :refIds" )
    List<Person> findByRefIds(List<UUID> refIds);
}
