package com.bigitcompany.cloudaireadmodel.common.connectors.snapshotevents;

import java.util.List;

public class RequestDto {

    private final String domain;
    private final List<TenantDto> tenants;
    private final List<RequestItemDto> entities;
    private final String topic;


    public RequestDto(String domain,
                      List<TenantDto> tenants,
                      List<RequestItemDto> entities,
                      String topic) {
        this.domain = domain;
        this.tenants = tenants;
        this.entities = entities;
        this.topic = topic;
    }

    public String getDomain() {
        return domain;
    }

    public List<TenantDto> getTenants() {
        return tenants;
    }

    public List<RequestItemDto> getEntities() {
        return entities;
    }

    public String getTopic() {
        return topic;
    }
}