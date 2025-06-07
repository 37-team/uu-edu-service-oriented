package com.bigitcompany.cloudaireadmodel.common.domain.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.annotations.VisibleForTesting;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.persistence.spring.ConfigurationRepository;
import com.sap.fsm.springboot.starter.db.infrastructure.persistence.multitenancy.CustomCurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

@Service
public class ActiveTenantProvider {

    private final ConfigurationRepository configurationRepository;

    private final CustomCurrentTenantIdentifierResolver customCurrentTenantIdentifierResolver;

    private final Cache<Tenant, Boolean> indexingEnabledCache;

    private final int cacheExpireSeconds;

    public ActiveTenantProvider(ConfigurationRepository configurationRepository,
                                CustomCurrentTenantIdentifierResolver customCurrentTenantIdentifierResolver,
                                @Value("${caches.expire-seconds.active-tenant}") int cacheExpireSeconds) {
        this.customCurrentTenantIdentifierResolver = customCurrentTenantIdentifierResolver;
        this.cacheExpireSeconds = cacheExpireSeconds;

        indexingEnabledCache = Caffeine.newBuilder()
            .expireAfterWrite(cacheExpireSeconds, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();
        this.configurationRepository = configurationRepository;
    }

    @Transactional(readOnly = true)
    public boolean isTenantEnabled(Tenant tenant) {
        Boolean enabledInCache = indexingEnabledCache.getIfPresent(tenant);

        if(enabledInCache != null) {
            return enabledInCache;
        }

        var enabled = false;

        var tenantConfigurationJpa = configurationRepository.findConfigurationByAccountIdAndCompanyId(tenant.getAccountId(), tenant.getCompanyId());

        if(tenantConfigurationJpa.isPresent() && tenantConfigurationJpa.get().isIndexingEnabled()) {
            enabled = true;
        }

        indexingEnabledCache.put(tenant, enabled);

        return enabled;
    }

    public String resolveCurrentTenantIdentifier() {
        return customCurrentTenantIdentifierResolver.resolveCurrentTenantIdentifier();
    }

    public int getCacheExpireSeconds() {
        return cacheExpireSeconds;
    }

    @VisibleForTesting
    public void invalidateInternalCache() {
        indexingEnabledCache.cleanUp();
        indexingEnabledCache.invalidateAll();
    }

}
