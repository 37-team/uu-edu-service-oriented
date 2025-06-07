package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

public class ResourceOptions {
    public static final boolean DEFAULT_GEOCODED_ONLY = false;
    public static final boolean DEFAULT_INCLUDE_INTERNAL_PERSONS = true;
    public static final boolean DEFAULT_INCLUDE_CROWD_PERSONS = false;
    public static final boolean DEFAULT_HOLIDAY_ENABLED = true;
    private static final boolean DEFAULT_CACHE_HOLIDAYS = true;

    private final boolean geocodedOnly;
    private final boolean includeInternalPersons;
    private final boolean includeCrowdPersons;
    private final boolean holidaysEnabled;
    private final boolean cacheHolidays;


    private ResourceOptions(boolean geocodedOnly, boolean includeInternalPersons, boolean includeCrowdPersons, boolean holidaysEnabled, boolean cacheHolidays) {
        this.geocodedOnly = geocodedOnly;
        this.includeInternalPersons = includeInternalPersons;
        this.includeCrowdPersons = includeCrowdPersons;
        this.holidaysEnabled = holidaysEnabled;
        this.cacheHolidays = cacheHolidays;
    }

    public boolean isGeocodedOnly() {
            return geocodedOnly;
    }

    public boolean isIncludeInternalPersons() {
        return includeInternalPersons;
    }

    public boolean isIncludeCrowdPersons() {
        return includeCrowdPersons;
    }

    public boolean isHolidaysEnabled() {
        return holidaysEnabled;
    }

    public boolean isCacheHolidays() {
        return cacheHolidays;
    }

    public static final class Builder {

        private boolean geocodedOnly;
        private boolean includeInternalPersons;
        private boolean includeCrowdPersons;
        private boolean holidaysEnabled;
        private boolean cacheHolidays;

        public Builder() {
            this.geocodedOnly = DEFAULT_GEOCODED_ONLY;
            this.includeInternalPersons = DEFAULT_INCLUDE_INTERNAL_PERSONS;
            this.includeCrowdPersons = DEFAULT_INCLUDE_CROWD_PERSONS;
            this.holidaysEnabled = DEFAULT_HOLIDAY_ENABLED;
            this.cacheHolidays = DEFAULT_CACHE_HOLIDAYS;
        }

        public Builder geocodedOnly(Boolean geocodedOnly) {
            if (geocodedOnly != null) {
                this.geocodedOnly = geocodedOnly;
            }
            return this;
        }

        public Builder includeInternalPersons(Boolean includeInternalPersons) {
            if (includeInternalPersons != null) {
                this.includeInternalPersons = includeInternalPersons;
            }
            return this;
        }

        public Builder holidaysEnabled(Boolean holidaysEnabled) {
            if (holidaysEnabled != null) {
                this.holidaysEnabled = holidaysEnabled;
            }
            return this;
        }

        public Builder cacheHolidays(Boolean cacheHolidays) {
            if (cacheHolidays != null) {
                this.cacheHolidays = cacheHolidays;
            }
            return this;
        }

        public Builder includeCrowdPersons(Boolean includeCrowdPersons) {
            if (includeCrowdPersons != null) {
                this.includeCrowdPersons = includeCrowdPersons;
            }
            return this;
        }

        public ResourceOptions build() {
            return new ResourceOptions(
                    this.geocodedOnly,
                    this.includeInternalPersons,
                    this.includeCrowdPersons,
                    this.holidaysEnabled,
                    this.cacheHolidays);
        }

    }
}
