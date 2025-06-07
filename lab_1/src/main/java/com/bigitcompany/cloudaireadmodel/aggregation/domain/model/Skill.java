package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;

import java.util.UUID;

public record Skill(UUID id, String name, WorkTimePattern validityPattern, UUID personId, Proficiency proficiency) {
}
