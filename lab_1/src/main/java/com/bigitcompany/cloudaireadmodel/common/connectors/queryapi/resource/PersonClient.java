package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.resource;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.PersonsFilter;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.DateTimeService;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue.udfValuesToMap;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;
import static java.util.Objects.nonNull;

@Component
public class PersonClient extends AbstractQueryApiClient {

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(PERSON);

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PERSON_FILTER_QUERY = "AND person.id IN ('%s') ";

    private static final String ORDER_BY_CLAUSE = "ORDER BY person.id ASC ";

    public PersonClient(@Value("${service.query-api.host}") String queryApiHost,
                        @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    public List<PersonQueryApi> queryPersonsSync(ReadModelRequestContext requestContext, PersonsFilter filter, int limit) {
        var body = createQueryBody(filter);
        var page = fetchFirstPageWithLimit(requestContext, REQUEST_DTOS, body, PersonQueryApiDto.class, limit);
        return unwrapPages(page);
    }

    private Map<String, String> createQueryBody(PersonsFilter filter) {
        var sqlSelect = new StringBuilder("""
                SELECT
                    person.id,\s
                    person.maxDistanceRadius,\s
                    person.locationLastUserChangedDate,\s
                    person.location,\s
                    person.externalId,\s
                    person.crowdType,\s
                    person.udfValues\s
                FROM
                    Person person\s
                WHERE
                    person.type='ERPUSER' AND\s
                    person.inactive != true AND\s
                    person.plannableResource = true\s
            """);

        if (!filter.ids().isEmpty()) {
            sqlSelect.append(String.format(PERSON_FILTER_QUERY, String.join("','", filter.ids().stream().map(UuidMapper::toFsmId).toList())));
        }
        if (filter.includeCrowdPersons() && !filter.includeInternalPersons()) {
            sqlSelect.append("AND person.crowdType IS NOT NULL AND person.crowdType != 'NON_CROWD' ");
        } else if (filter.includeInternalPersons() && !filter.includeCrowdPersons()) {
            sqlSelect.append("AND (person.crowdType IS NULL OR person.crowdType = 'NON_CROWD') ");
        }
        sqlSelect.append(ORDER_BY_CLAUSE);
        return Map.of("query", sqlSelect.toString());
    }

    private List<PersonQueryApi> unwrapPages(PersonQueryApiDto page) {
        return page.getData().stream()
            .map(personData -> {
                return createPersonQueryApi(personData.getPerson());
            })
            .toList();
    }

    private PersonQueryApi createPersonQueryApi(PersonQueryApiDto.Person person) {
        return new PersonQueryApi(
            UuidMapper.toUUID(person.id()),
            nonNull(person.externalId()) ? person.externalId() : null,
            person.maxDistanceRadius(),
            nonNull(person.location()) ? new Location(person.location().latitude(), person.location().longitude()) : null,
            DateTimeService.toInstant(person.locationLastUserChangedDate()),
            person.crowdType(),
            udfValuesToMap(person.udfValues())
        );
    }
}
