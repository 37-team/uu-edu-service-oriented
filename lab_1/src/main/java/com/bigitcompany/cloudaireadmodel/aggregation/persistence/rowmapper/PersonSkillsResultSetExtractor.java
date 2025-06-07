package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Proficiency;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.SkillWorkTimePatternMapper;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
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

import static com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.RowMapperHelper.mapPgObjectTo;

public class PersonSkillsResultSetExtractor implements ResultSetExtractor<Map<UUID, List<Skill>>> {

    private static final Logger logger = LoggerFactory.getLogger(PersonSkillsResultSetExtractor.class);


    @Override
    public Map<UUID, List<Skill>> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<UUID, List<Skill>> personSkillsMap = new HashMap<>();

        while (rs.next()) {

            var personId = UuidMapper.toUUID(rs.getString("skill_person"));
            var skill = createSkillFromResultSet(rs);

            if (personSkillsMap.containsKey(personId)) {
                personSkillsMap.get(personId).add(skill);
            } else {
                List<Skill> skills = new ArrayList<>();
                skills.add(skill);
                personSkillsMap.put(personId, skills);
            }
        }

        return personSkillsMap;
    }

    private Skill createSkillFromResultSet(ResultSet rs) throws SQLException {

        var id = UuidMapper.toUUID(rs.getString("skill_id"));
        var startDate = rs.getString("skill_start");
        var endDate = rs.getString("skill_end");
        var startTime = rs.getString("skill_starttime");
        var endTime = rs.getString("skill_endtime");
        var skillName = rs.getString("tag_name");
        var days = extractDaysList(rs, "skill_days");
        var personId = UuidMapper.toUUID(rs.getString("skill_person"));

        var startInstantDate = startDate != null ? DateTimeService.fromStringDateToInstant(startDate, DateTimeService.START_DATE_FORMATTER) : null;
        var endInstantDate = endDate != null ? DateTimeService.fromStringDateToInstant(endDate, DateTimeService.END_DATE_FORMATTER) : null;

        return new Skill(
            id,
            skillName,
            SkillWorkTimePatternMapper.toWorkTimePattern(startInstantDate, endInstantDate, DateTimeService.fromStringTimeToLocalTime(startTime), DateTimeService.fromStringTimeToLocalTime(endTime), days),
            personId,
            //TODO FSMCPB-91040: fetch actual proficiency
            Proficiency.TECHNICIAN_PROFICIENCY
        );
    }

    private List<String> extractDaysList(ResultSet rs, String columnName) {
        List<String> days;
        try {
            days = mapPgObjectTo(rs.getObject(columnName), List.class);
        } catch (SQLException | JsonProcessingException e) {
            var message = "Error processing days for resource.";
            logger.error(message + e.getMessage(), e);
            throw new DomainException(message);
        }
        return days;
    }
}
