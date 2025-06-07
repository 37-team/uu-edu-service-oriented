package com.bigitcompany.cloudaireadmodel.common.connectors.cloud.skill;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Proficiency;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class SkillMicroserviceClient {

    private SkillProficiencyLevelConnector skillProficiencyLevelConnector;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public SkillMicroserviceClient(SkillProficiencyLevelConnector skillProficiencyLevelConnector) {
        this.skillProficiencyLevelConnector = skillProficiencyLevelConnector;
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, List<Skill>>> fetchSkillsWithProficienciesAsync(Map<UUID, List<Skill>> skillsWithoutProficiencies) {
        var skillIds = skillsWithoutProficiencies.values().stream().flatMap(skills -> skills.stream().map(Skill::id)).toList();

        Map<UUID, Integer> proficiencies;
        try {
            proficiencies = skillProficiencyLevelConnector.fetchSkillsProficiencies(skillIds);
        } catch (WebClientRequestException e) {
            LOG.error("Error while fetching skill proficiencies", e);
            proficiencies = new HashMap<>();
        }

        Map<UUID, List<Skill>> skillsMapWithProficiencies = mergeSkillsWithProficiencies(skillsWithoutProficiencies, proficiencies);

        return CompletableFuture.completedFuture(skillsMapWithProficiencies);
    }

    private Map<UUID, List<Skill>> mergeSkillsWithProficiencies(Map<UUID, List<Skill>> skillsWithoutProficiency, Map<UUID, Integer> proficiencies) {
        return skillsWithoutProficiency.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
            var personId = entry.getKey();
            var skills = entry.getValue();
            return skills.stream().map(skill -> {
                var proficiency = proficiencies.containsKey(skill.id()) ? new Proficiency(proficiencies.get(skill.id())) : null;
                return new Skill(skill.id(), skill.name(), skill.validityPattern(), personId, proficiency);
            }).toList();
        }));
    }

}
