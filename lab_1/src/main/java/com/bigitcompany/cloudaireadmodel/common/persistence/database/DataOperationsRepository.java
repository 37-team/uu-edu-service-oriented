package com.bigitcompany.cloudaireadmodel.common.persistence.database;

import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class DataOperationsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void truncateAllTables(Tenant tenant) {
        List<String> allTablesWithSchema = Arrays.stream(SupportedFsmDtos.values())
            .map(dto -> tenant.getSchema() + "." + dto.getTableName())
            .toList();
        String truncateQuery = String.format("truncate table %s", String.join(", ", allTablesWithSchema));
        entityManager.createNativeQuery(truncateQuery).executeUpdate();
    }

    public Integer getObjectCount(Tenant tenant, String dtoName) {
        var table = SupportedFsmDtos.getByName(dtoName).getTableName();
        var query = String.format("SELECT COUNT(*) FROM %s.%s;", tenant.getSchema(), table);

        /*
         * As of late 2020 maximum number of objects of a single type should not exceed 500,000,000
         * Requirements: https://confluence.coresystems.net/display/cloud/Target+Non-Functional+Requirements
         */
        return ((Long) entityManager.createNativeQuery(query).getSingleResult()).intValue();
    }
}