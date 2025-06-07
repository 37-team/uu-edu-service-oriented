package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityResultSetExtractor implements ResultSetExtractor<Entity> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Entity extractData(ResultSet rs) throws SQLException {

        Entity entity = null;
        UUID id = null;
        String externalId = null;
        Map<String, String> udfs = new HashMap<>();
        while (rs.next()) {
            udfs.put(rs.getString("udfName"), rs.getString("udfValue"));
            if (rs.isFirst()) {
                id = UuidMapper.toUUID(rs.getString("id"));
                externalId = rs.getString("externalId");
            }
        }
        if (id != null) {
            entity = new Entity(id, externalId, udfs);
        }
        return entity;
    }
}