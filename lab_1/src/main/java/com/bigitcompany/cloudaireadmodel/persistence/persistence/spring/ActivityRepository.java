package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Activity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ActivityRepository extends CrudRepository<Activity, UUID> {
    @Query( nativeQuery = true, value = "select a.* from Activity a where a.id in :ids" )
    Optional<List<Activity>> findByIds(List<UUID> ids);

}