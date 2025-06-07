package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingsFilter {

    private static final List<String> DEFAULT_ACTIVITIES_TO_EXCLUDE = new ArrayList<>();

    private static final boolean DEFAULT_CONSIDER_RELEASED_AS_EXCLUSIVE = true;

    private static final boolean DEFAULT_CONSIDER_PLANNED_AS_EXCLUSIVE = false;

    private final Instant earliest;

    private final Instant latest;

    private final List<String> activitiesToExclude;

    private final boolean considerReleasedAsExclusive;

    private final boolean considerPlannedAsExclusive;

    private BookingsFilter(Instant earliest, Instant latest, List<String> activitiesToExclude, boolean considerReleasedAsExclusive, boolean considerPlannedAsExclusive) {
        this.earliest = earliest;
        this.latest = latest;
        this.activitiesToExclude = activitiesToExclude;
        this.considerReleasedAsExclusive = considerReleasedAsExclusive;
        this.considerPlannedAsExclusive = considerPlannedAsExclusive;
    }

    public Instant getEarliest() {
        return earliest;
    }

    public Instant getLatest() {
        return latest;
    }

    /**
     * Note: returns a read-only collection, to update state a mutator method should be used.
     */
    public List<String> getActivitiesToExclude() {
        return Collections.unmodifiableList(activitiesToExclude);
    }

    public boolean isConsiderReleasedAsExclusive() {
        return considerReleasedAsExclusive;
    }

    public boolean isConsiderPlannedAsExclusive() {
        return considerPlannedAsExclusive;
    }

    public static final class Builder {
        private Instant earliest;

        private Instant latest;

        private List<String> activitiesToExclude;

        private boolean considerReleasedAsExclusive;

        private boolean considerPlannedAsExclusive;

        public Builder(Instant earliest, Instant latest) {
            this.earliest = earliest;
            this.latest = latest;
            activitiesToExclude = DEFAULT_ACTIVITIES_TO_EXCLUDE;
            considerReleasedAsExclusive = DEFAULT_CONSIDER_RELEASED_AS_EXCLUSIVE;
            considerPlannedAsExclusive = DEFAULT_CONSIDER_PLANNED_AS_EXCLUSIVE;
        }

        public Builder earliest(Instant earliest) {
            if (earliest != null) {
                this.earliest = earliest;
            }
            return this;
        }

        public Builder latest(Instant latest) {
            if (latest != null) {
                this.latest = latest;
            }
            return this;
        }

        public Builder activitiesToExclude(List<String> activitiesToExclude) {
            if (activitiesToExclude != null) {
                this.activitiesToExclude = activitiesToExclude;
            }
            return this;
        }

        public Builder considerReleasedAsExclusive(Boolean considerReleasedAsExclusive) {
            if (considerReleasedAsExclusive != null) {
                this.considerReleasedAsExclusive = considerReleasedAsExclusive;
            }
            return this;
        }

        public Builder considerPlannedAsExclusive(Boolean considerPlannedAsExclusive) {
            if (considerPlannedAsExclusive != null) {
                this.considerPlannedAsExclusive = considerPlannedAsExclusive;
            }
            return this;
        }

        public BookingsFilter build() {
            return new BookingsFilter(earliest, latest, activitiesToExclude, considerReleasedAsExclusive, considerPlannedAsExclusive);
        }
    }
}
