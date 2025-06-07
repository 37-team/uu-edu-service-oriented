package com.bigitcompany.cloudaireadmodel.common.domain.services;

import com.bigitcompany.cloudaireadmodel.common.connectors.snapshotevents.TechnicalEventsServiceConnector;
import com.bigitcompany.cloudaireadmodel.common.persistence.jpa.Configuration;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.persistence.database.DataOperationsRepository;
import com.bigitcompany.cloudaireadmodel.common.persistence.jpa.ConfigurationEvent;
import com.bigitcompany.cloudaireadmodel.common.persistence.spring.ConfigurationHistoryRepository;
import com.bigitcompany.cloudaireadmodel.common.persistence.spring.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ConfigurationService {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ConfigurationRepository configurationRepository;

    private final ConfigurationHistoryRepository historyRepository;

    private final TechnicalEventsServiceConnector technicalEventsServiceConnector;

    private final DataOperationsRepository dataOperationsRepository;

    private final ActiveTenantProvider activeTenantProvider;

    private final ScheduledExecutorService executorService;

    public static final int ADDITIONAL_DELAY_IN_SECONDS = 1;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository,
                                ConfigurationHistoryRepository historyRepository,
                                TechnicalEventsServiceConnector technicalEventsServiceConnector,
                                DataOperationsRepository dataOperationsRepository,
                                ActiveTenantProvider activeTenantProvider) {
        this.configurationRepository = configurationRepository;
        this.technicalEventsServiceConnector = technicalEventsServiceConnector;
        this.dataOperationsRepository = dataOperationsRepository;
        this.historyRepository = historyRepository;
        this.activeTenantProvider = activeTenantProvider;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Transactional(readOnly = true)
    public Optional<com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration> getTenantConfiguration(Tenant tenant) {
        var tenantConfigurationJpa = configurationRepository.findConfigurationByAccountIdAndCompanyId(tenant.getAccountId(), tenant.getCompanyId());

        com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration configuration = null;
        if (tenantConfigurationJpa.isPresent()) {
            configuration = new com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration(tenantConfigurationJpa.get());
        }
        return Optional.ofNullable(configuration);
    }

    @Transactional(readOnly = true)
    public List<ConfigurationEvent> getHistory(Tenant tenant) {
        return historyRepository
            .findTenantConfigurationHistoryByAccountIdAndCompanyId(tenant.getAccountId(), tenant.getCompanyId())
            .orElse(Collections.emptyList());
    }

    @Transactional
    public UUID createTenantConfiguration(
            com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration configuration) {
        LOG.debug("Creating configuration: {}", configuration);
        var newConfiguration = formatConfigurationJpa(configuration);
        final var savedConfiguration = configurationRepository.save(newConfiguration);
        final var history = formatHistoryEntryJpa(configuration);
        historyRepository.save(history);

        if (savedConfiguration.isIndexingEnabled()) {
            invokeSnapshotsWithDelay(configuration.getTenant());
        }

        LOG.debug("Configuration has been created");
        return savedConfiguration.getId();
    }

    @Transactional
    public void updateTenantConfiguration(com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration update) {
        LOG.debug("Updating configuration with request dto for request : {}", update);

        var existing = configurationRepository.findConfigurationByAccountIdAndCompanyId(update.getTenant().getAccountId(), update.getTenant().getCompanyId());

        if (existing.isPresent()) {
            var current = existing.get();

            if (update.isIndexingEnabled() != current.isIndexingEnabled()) {
                if (update.isIndexingEnabled() && !current.isIndexingEnabled()) {
                    invokeSnapshotsWithDelay(current.getTenant());
                } else if (current.isIndexingEnabled() && !update.isIndexingEnabled()) {
                    truncateData(update);
                    LOG.info("All data for tenant with account {} and company {} deleted!", update.getTenant().getAccountId(), update.getTenant().getCompanyId());
                }
                current.setIndexingEnabled(update.isIndexingEnabled());
            }

            if (update.isQueryingEnabled() != current.isQueryingEnabled()) {
                current.setQueryingEnabled(update.isQueryingEnabled());
            }

            configurationRepository.save(current);
            final var history = formatHistoryEntryJpa(update);
            historyRepository.save(history);
        }
    }

    @Transactional
    public void deleteConfiguration(com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration configuration) {
        truncateData(configuration);
        configurationRepository.deleteByAccountIdAndCompanyId(configuration.getTenant().getAccountId(), configuration.getTenant().getCompanyId());
        final var placeholder = new com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration(configuration.getTenant());
        final var history = formatHistoryEntryJpa(placeholder);
        historyRepository.save(history);
        LOG.info("All data and configuration for tenant with account {} and company {} deleted!", configuration.getTenant().getAccountId(), configuration.getTenant().getCompanyId());
    }

    public com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration mergeConfigurationForUpdate(
            com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration existing, Boolean isIndexingEnabled, Boolean isQueryingEnabled) {
        return new com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration(
            existing.getTenant(),
            isIndexingEnabled != null ? isIndexingEnabled : existing.isIndexingEnabled(),
            isQueryingEnabled != null ? isQueryingEnabled : existing.isQueryingEnabled()
        );
    }

    private void truncateData(com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration configuration) {
        LOG.warn("Truncating all data for tenant with account {} and company {}", configuration.getTenant().getAccountId(), configuration.getTenant().getCompanyId());
        dataOperationsRepository.truncateAllTables(configuration.getTenant());
    }

    private ConfigurationEvent formatHistoryEntryJpa(
            com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration configuration) {
        return new ConfigurationEvent(
            configuration.getTenant().getAccountId(),
            configuration.getTenant().getCompanyId(),
            configuration.isIndexingEnabled(),
            configuration.isQueryingEnabled()
        );
    }

    private Configuration formatConfigurationJpa(
            com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration configuration) {
        return new Configuration(
            configuration.getTenant().getAccountId(),
            configuration.getTenant().getCompanyId(),
            configuration.isIndexingEnabled(),
            configuration.isQueryingEnabled()
        );
    }

    private void invokeSnapshotsWithDelay(Tenant tenant) {

        var waitInSeconds = this.activeTenantProvider.getCacheExpireSeconds() + ADDITIONAL_DELAY_IN_SECONDS;
        LOG.debug("Waiting {} seconds before enabling tenant a{}_{}", waitInSeconds, tenant.getAccountId(), tenant.getCompanyId());

        executorService.schedule(() -> technicalEventsServiceConnector.enableSnapshotEventsForTenant(tenant), waitInSeconds, TimeUnit.SECONDS);
    }
}