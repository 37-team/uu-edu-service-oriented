package com.bigitcompany.cloudaireadmodel.persistence.persistence.records;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.jpa.Skill;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.spring.SkillRepository;
import org.springframework.stereotype.Component;

import jakarta.persistence.NoResultException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class SkillService implements RecordAccessService {

    private final SkillRepository repository;

    public SkillService(SkillRepository repository) {
        this.repository = repository;
    }

    public Optional<Skill> read(UUID id) {
        return this.repository.findById(id);
    }

    @Override
    public void saveAll(List<Map<String, Object>> skillRecords) {
        List<Skill> entities = skillRecords.stream().map(Skill::new).toList();
        this.repository.saveAll(entities);
    }

    @Override
    public void save(Map<String, Object> skillRecord) {
        var skill = new Skill(skillRecord);
        this.repository.save(skill);
    }

    @Override
    public void update(UUID id, Map<String, Object> skillRecord) {
        Optional<Skill> existing = this.repository.findById(id);

        if (existing.isEmpty()) {
            throw new NoResultException("Cannot update non-existent entity");
        }

        Skill updated = existing.get().copy(skillRecord);
        this.repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        this.repository.deleteById(id);
    }

    public List<Skill> findByPersonIds(List<UUID> personIds) {
        return repository.findByPersonIds(personIds);
    }
}