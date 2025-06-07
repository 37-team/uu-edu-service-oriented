package com.bigitcompany.cloudaireadmodel.aggregation.persistence;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.FetchesFilter;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Skill;
import com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.PersonSkillsResultSetExtractor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Component
public class SkillDataRepository {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String FETCH_SKILLS =
            " SELECT " +
                    "   s.id as skill_id, s.person as skill_person, s.startDate as skill_start, s.endDate as skill_end, t.name as tag_name, " +
                    "   s.startTime as skill_starttime, s.endTime as skill_endtime, s.days as skill_days " +
                    "      FROM " +
                    "         Skill s, Tag t " +
                    " WHERE " +
                    "  s.person = ANY (?) AND " +
                    "  s.tag = t.id ";

    private final JdbcTemplate jdbcTemplate;
    private final ResultSetExtractor<Map<UUID, List<Skill>>> skillResultSetExtractor;

    public SkillDataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.skillResultSetExtractor = new PersonSkillsResultSetExtractor();
    }

    public Map<UUID, List<Skill>> fetchSkills(List<UUID> personIds, String tenant, FetchesFilter fetchesFilter) {
        if (fetchesFilter.doNotReturn()) {
            LOG.debug("No skills returned for person because of an empty fetch skill map.");
            return new HashMap<>();
        }
        var skillsNames = fetchesFilter.getNames();
        PreparedStatementCreator skillAndTagStatement = new TenantSqlPreparedStatementProvider(tenant)
            .appendSingleArgWithQuery(FETCH_SKILLS, personIds.toArray(UUID[]::new))
            .appendOptionalArg(" AND t.name = ANY (?)", skillsNames.isEmpty() ? null : skillsNames.toArray(String[]::new));

        return  fetchPersonSkillsAsMap(skillAndTagStatement);

    }

    private Map<UUID, List<Skill>> fetchPersonSkillsAsMap(PreparedStatementCreator statementCreator) {
        Map<UUID, List<Skill>> skillsToMap = jdbcTemplate.query(statementCreator, skillResultSetExtractor);
        return Objects.requireNonNullElseGet(skillsToMap, HashMap::new);
    }

}
