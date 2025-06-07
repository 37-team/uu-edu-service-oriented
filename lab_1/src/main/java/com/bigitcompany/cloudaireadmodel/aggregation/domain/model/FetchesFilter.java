package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.util.Collections;
import java.util.Set;

public class FetchesFilter {

    private final Set<String> names;

    private boolean doNotReturn;

    public FetchesFilter(Set<String> names, boolean doNotReturn) {
        this.names = names;
        this.doNotReturn = names != null && names.isEmpty() || doNotReturn;
    }

    /**
     * Returns true if fetches (udfs or skills) given name should be returned
     * If no filter was provided assumes all fetches should be returned
     * @param name
     * @return
     */
    public boolean shouldInclude(String name) {
        if (names == null) {
            return true;
        }
        return names.contains(name);
    }

    public boolean doNotReturn() {
        return doNotReturn;
    }

    public Set<String> getNames() {
        if (names == null) {
            return Collections.emptySet();
        }
        return names;
    }
}
