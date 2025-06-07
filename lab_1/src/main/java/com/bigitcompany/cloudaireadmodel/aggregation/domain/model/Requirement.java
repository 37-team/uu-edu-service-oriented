package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.util.UUID;

    public record Requirement(String tag, UUID activity, boolean mandatory, Proficiency proficiency) {
}
