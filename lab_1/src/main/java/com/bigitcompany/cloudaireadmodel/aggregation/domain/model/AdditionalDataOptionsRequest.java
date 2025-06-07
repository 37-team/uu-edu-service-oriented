package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

public class AdditionalDataOptionsRequest {

    public static final Boolean DEFAULT_USE_EXCLUDE_LIST = false;

    private boolean useExcludeList;

    private AdditionalDataOptionsRequest(boolean useExcludeList) {
        this.useExcludeList = useExcludeList;
    }

    public boolean isUseExcludeList() {
        return useExcludeList;
    }

    public static final class Builder {

        private boolean useExcludeList;

        public Builder() {
            this.useExcludeList = DEFAULT_USE_EXCLUDE_LIST;
        }

        public Builder useExcludeList(Boolean useExcludeList) {
            if (useExcludeList != null) {
                this.useExcludeList = useExcludeList;
            }
            return this;
        }

        public AdditionalDataOptionsRequest build() {
            return new AdditionalDataOptionsRequest(
                    this.useExcludeList
            );
        }
    }

}
