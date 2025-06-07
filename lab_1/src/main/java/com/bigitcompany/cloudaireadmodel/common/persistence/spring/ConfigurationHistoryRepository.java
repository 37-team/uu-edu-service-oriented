package com.bigitcompany.cloudaireadmodel.common.persistence.spring;

import com.bigitcompany.cloudaireadmodel.common.persistence.jpa.ConfigurationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationHistoryRepository extends JpaRepository<ConfigurationEvent, UUID> {

    Optional<List<ConfigurationEvent>> findTenantConfigurationHistoryByAccountIdAndCompanyId(Long accountId, Long companyId);

}