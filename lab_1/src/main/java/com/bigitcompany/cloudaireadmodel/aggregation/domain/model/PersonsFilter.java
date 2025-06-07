package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;


import java.util.Collections;
import java.util.List;

public record PersonsFilter(List<String> ids,
                            boolean includeInternalPersons,
                            boolean includeCrowdPersons) {

    public PersonsFilter() {
        this(Collections.emptyList(), true, false);
    }
}