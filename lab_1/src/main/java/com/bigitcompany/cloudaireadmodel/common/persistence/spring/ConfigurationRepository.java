package com.bigitcompany.cloudaireadmodel.common.persistence.spring;

import com.bigitcompany.cloudaireadmodel.common.persistence.jpa.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, UUID> {

    Optional<Configuration> findConfigurationByAccountIdAndCompanyId(Long accountId, Long companyId);

    @Transactional
    void deleteByAccountIdAndCompanyId(Long accountId, Long companyId);
}