package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Tag;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.TagRepository;
import org.springframework.stereotype.Component;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class TagService implements RecordAccessService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public Optional<Tag> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> tagRecords) {
        List<Tag> entities = tagRecords.stream().map(Tag::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> tagRecord) {
        var tag = new Tag(tagRecord);
        this.repository.save(tag);
    }

    @Override
    public void update(UUID id, Map<String, Object> tagRecord) {
        Optional<Tag> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }

        Tag updated = existing.get().copy(tagRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }
}