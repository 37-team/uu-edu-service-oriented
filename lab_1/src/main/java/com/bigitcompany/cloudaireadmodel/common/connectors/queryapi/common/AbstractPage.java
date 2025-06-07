package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common;

import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;

public abstract class AbstractPage {
    private boolean truncated;

    private int pageSize;

    private int currentPage;

    private int lastPage;

    private int totalObjectCount;

    protected AbstractPage(boolean truncated, int pageSize, int currentPage, int lastPage, int totalObjectCount) {
        this.truncated = truncated;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.lastPage = lastPage;
        this.totalObjectCount = totalObjectCount;
    }

    protected AbstractPage() {
        // For jackson
    }

    @Generated
    public boolean isTruncated() {
        return truncated;
    }

    @Generated
    public int getPageSize() {
        return pageSize;
    }


    @Generated
    public int getCurrentPage() {
        return currentPage;
    }


    @Generated
    public int getLastPage() {
        return lastPage;
    }


    @Generated
    public int getTotalObjectCount() {
        return totalObjectCount;
    }
}