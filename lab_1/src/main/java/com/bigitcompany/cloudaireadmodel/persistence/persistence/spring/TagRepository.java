package com.bigitcompany.cloudaireadmodel.persistence.persistence.spring;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Tag;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TagRepository extends CrudRepository<Tag, UUID> {
}
