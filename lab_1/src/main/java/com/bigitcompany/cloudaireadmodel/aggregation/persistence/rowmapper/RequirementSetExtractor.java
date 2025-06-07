package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Proficiency;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Requirement;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RequirementSetExtractor implements ResultSetExtractor<Map<UUID, List<Requirement>>> {

    private static final Logger logger = LoggerFactory.getLogger(RequirementSetExtractor.class);

    @Override
    public Map<UUID, List<Requirement>> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<UUID, List<Requirement>> map = new HashMap<>();

        while (rs.next()) {

            // extract requirement values
            var mandatory = rs.getBoolean("requirement_mandatory");
            var activity = UuidMapper.toUUID(rs.getString("requirement_activity"));

            // extract tag values
            var name = rs.getString("tag_name");

            //TODO FSMCPB-91040: fetch actual proficiency
            var req = new Requirement(name, activity, mandatory, Proficiency.REQUIRED_PROFICIENCY);

            map.computeIfAbsent(activity, k -> new ArrayList<>());
            map.get(activity).add(req);
        }

        return map;
    }

}
