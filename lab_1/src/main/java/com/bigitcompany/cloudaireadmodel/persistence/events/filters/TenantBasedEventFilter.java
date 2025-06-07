package com.bigitcompany.cloudaireadmodel.persistence.events.filters;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.sap.fsm.springboot.starter.events.domain.Event;
import com.sap.fsm.springboot.starter.events.infrastructure.event.filter.EventFilter;

public class TenantBasedEventFilter implements EventFilter {

    private final ActiveTenantProvider provider;

    public TenantBasedEventFilter(ActiveTenantProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean shouldProcess(Event event) {
        return provider.isTenantEnabled(new Tenant(event.getAccountId(), event.getCompanyId()));
    }
}