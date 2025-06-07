package com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Resource;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainException;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WeekPattern;
import com.bigitcompany.cloudaireadmodel.common.domain.model.wtp.WorkTimePattern;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.bigitcompany.cloudaireadmodel.aggregation.persistence.rowmapper.RowMapperHelper.mapPgObjectTo;

public class ResourcesRowMapper implements RowMapper<Resource> {


    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {

        List<WorkTimePattern> patterns = this.mapPatterns(rs.getString("resource_wtp"));

            return new Resource.Builder()
                    .id(UUID.fromString(rs.getString("id")))
                    .lastChanged(rs.getTimestamp("lastchanged"))
                    .externalId(rs.getString("externalid"))
                    .origin(rs.getString("crowdtype"))
                    .locationLastUserChangedDate(rs.getTimestamp("locationlastuserchangeddate"))
                    .realTimeLocation(extractLocation(rs))
                    .worktimePattern(patterns)
                    .build();
    }

    private List<WeekPattern> extractWeeks(Object o) {
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        List<Object> objectList = objectMapper.convertValue(o, ArrayList.class);
        List<WeekPattern> weekPatterns = new ArrayList<>();
        for (Object listElement : objectList) {
            weekPatterns.add(objectMapper.convertValue(listElement, WeekPattern.class));
        }
        return weekPatterns;
    }


    private List<WorkTimePattern> mapPatterns(String patternsJson) {
        List<WorkTimePattern> workTimePatterns = new ArrayList<>();
        if (patternsJson == null) {
            return workTimePatterns;
        }
        var objectMapper = new ObjectMapper();

        try {
            List<Map<String, Object>> elements = objectMapper.readValue(patternsJson, ArrayList.class);
            for (Map<String, Object> element : elements) {
                if (element != null) {
                    var wtpStart = DateTimeService.toZonedDateTime((String) element.get("start")).toInstant();
                    var endDate = DateTimeService.toZonedDateTime((String) element.get("end"));
                    Instant wtpEnd = endDate == null? null : endDate.toInstant();
                    List<WeekPattern> weeks = extractWeeks(element.get("weeks"));
                    workTimePatterns.add(new WorkTimePattern(wtpStart, wtpEnd, weeks));
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error deserializing elements");
        }
        return workTimePatterns;

    }

    private Location extractLocation(ResultSet rs) {
        Location realTimeLocation;
        try {
            realTimeLocation = mapPgObjectTo(rs.getObject("location"), Location.class);
        } catch (SQLException | JsonProcessingException e) {
            var message = "Error processing location for resource.";
            LOG.error(message + e.getMessage(), e);
            throw new DomainException(message);
        }
        return realTimeLocation;
    }
}