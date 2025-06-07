package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UdfRowMapper implements RowMapper<Udf> {

    @Override
    public Udf mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Udf.Builder()
            .entityId(UuidMapper.toUUID(rs.getString("entity")))
            .metaId(UuidMapper.toUUID(rs.getString("meta_uuid")))
            .name(rs.getString("udf_name"))
            .value(rs.getString("udf_value"))
            .build();
    }
}