package com.bigitcompany.cloudaireadmodel.persistence.configuration;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ConfigurationService;
import com.sap.fsm.springboot.starter.db.infrastructure.persistence.AbstractTenant;
import com.sap.fsm.springboot.starter.tenantmanagement.domain.tenant.TenantService;
import com.sap.fsm.springboot.starter.tenantmanagement.domain.tenant.listeners.TenantPostChangeListener;
import org.springframework.stereotype.Service;

@Service
public class TenantPostChangeListenerImpl implements TenantPostChangeListener {

    private final ConfigurationService configurationService;

    public TenantPostChangeListenerImpl(ConfigurationService configurationService, TenantService tenantService) {
        this.configurationService = configurationService;
        tenantService.registerListener(this);
    }

    @Override
    public void afterDeactivate(AbstractTenant tenant) {
        configurationService.deleteConfiguration(
            new Configuration(new Tenant(tenant.getAccountId(), tenant.getCompanyId()), false, false)
        );
    }

    @Override
    public void afterActivate(AbstractTenant tenant) {
        // No custom actions necessary afterActivate
    }

    @Override
    public void afterSynchronization() {
        // No custom actions necessary afterSynchronization
    }
}