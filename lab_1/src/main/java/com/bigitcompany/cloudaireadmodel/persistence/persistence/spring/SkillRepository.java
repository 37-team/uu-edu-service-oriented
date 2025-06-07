package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Skill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SkillRepository extends CrudRepository<Skill, UUID> {
    @Query( nativeQuery = true, value = "select s.* from Skill s where s.person in :personIds" )
    List<Skill> findByPersonIds(List<UUID> personIds);
}
