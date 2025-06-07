package com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.skill.SkillClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SkillProxyService {

    private final SkillClient skillClient;

    public SkillProxyService(SkillClient skillClient) {
        this.skillClient = skillClient;
    }

    public Map<UUID, List<Skill>> getSkillsByPersonIds(List<UUID> personIds) {
        return skillClient.querySkillsByPersonIds(new ReadModelRequestContext(), personIds);
    }
}
