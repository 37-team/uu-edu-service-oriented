package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.location;

import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.ADDRESS;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.PERSON;

@Component
public class LocationClient extends AbstractQueryApiClient {
    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(PERSON, ADDRESS);

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public LocationClient(@Value("${service.query-api.host}") String queryApiHost,
                          @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    @Async("taskExecutorWithSecurityContext")
    public CompletableFuture<Map<UUID, Location>> queryLocationsByPersonIds(ReadModelRequestContext requestContext, List<UUID> personIds) {
        var body = createQueryBody(personIds);
        var pages = getAllPages(requestContext, REQUEST_DTOS, body, LocationQueryApiDto.class);
        return CompletableFuture.completedFuture(unwrapPagesByPersonId(pages));
    }

    private Map<String, String> createQueryBody(List<UUID> personIds) {
        String selectQuery = """
            SELECT\s
                person.id,\s
                employeeAddressHomeDefault.location,\s
                employeeAddressHomeDefault.type,\s
                employeeAddressWorkDefault.location,\s
                employeeAddressWorkDefault.type,\s
                erpUserAddressHomeDefault.location,\s
                erpUserAddressHomeDefault.type,\s
                erpUserAddressWorkDefault.location,\s
                erpUserAddressWorkDefault.type,\s
                employeeAddressHome.location,\s
                employeeAddressHome.type,\s
                employeeAddressWork.location,\s
                employeeAddressWork.type,\s
                erpUserAddressHome.location,\s
                erpUserAddressHome.type,\s
                erpUserAddressWork.location,\s
                erpUserAddressWork.type\s
            FROM Person person\s
                 LEFT JOIN Address erpUserAddressHomeDefault\s
                           ON erpUserAddressHomeDefault.object.objectId=person.id AND erpUserAddressHomeDefault.object.objectType='PERSON'\s
                                  AND erpUserAddressHomeDefault.type='HOME' AND erpUserAddressHomeDefault.defaultAddress = true\s
                 LEFT JOIN Address erpUserAddressWorkDefault\s
                           ON erpUserAddressWorkDefault.object.objectId=person.id AND erpUserAddressWorkDefault.object.objectType='PERSON'\s
                                  AND erpUserAddressWorkDefault.type='WORK' AND erpUserAddressWorkDefault.defaultAddress = true\s
                
                 LEFT JOIN Address erpUserAddressHome\s
                           ON erpUserAddressHome.object.objectId=person.id AND erpUserAddressHome.object.objectType='PERSON' AND erpUserAddressHome.type='HOME'\s
                 LEFT JOIN Address erpUserAddressWork\s
                           ON erpUserAddressWork.object.objectId=person.id AND erpUserAddressWork.object.objectType='PERSON' AND erpUserAddressWork.type='WORK'\s
                
                 LEFT JOIN Person personEmployee\s
                           ON personEmployee.refId = person.refId AND personEmployee.type='EMPLOYEE'\s
                 LEFT JOIN Address employeeAddressHomeDefault\s
                           ON employeeAddressHomeDefault.object.objectId=personEmployee.id AND employeeAddressHomeDefault.object.objectType='PERSON'\s
                                  AND employeeAddressHomeDefault.type='HOME' AND employeeAddressHomeDefault.defaultAddress = true\s
                 LEFT JOIN Address employeeAddressWorkDefault\s
                           ON employeeAddressWorkDefault.object.objectId=personEmployee.id AND employeeAddressWorkDefault.object.objectType='PERSON'\s
                                  AND employeeAddressWorkDefault.type='WORK' AND employeeAddressWorkDefault.defaultAddress = true\s
                
                 LEFT JOIN Address employeeAddressHome\s
                           ON employeeAddressHome.object.objectId=personEmployee.id AND employeeAddressHome.object.objectType='PERSON' AND employeeAddressHome.type='HOME'\s
                 LEFT JOIN Address employeeAddressWork\s
                           ON employeeAddressWork.object.objectId=personEmployee.id AND employeeAddressWork.object.objectType='PERSON' AND employeeAddressWork.type='WORK'\s
            WHERE person.id IN ('%s')\s
                  AND person.type = 'ERPUSER'\s
            ORDER BY person.id ASC\s
            """.formatted(String.join("','", personIds.stream().map(UuidMapper::toFsmId).toList()));
        return Map.of("query", selectQuery);
    }

    private Map<UUID, Location> unwrapPagesByPersonId(List<LocationQueryApiDto> pages) {
        Map<UUID, Location> locations = new HashMap<>();

        pages.stream()
            .flatMap(location -> location.getData().stream())
            .forEach(locationData -> {
                var person = locationData.person();

                List<Location> personLocationList = new ArrayList<>();

                // Order matters: first Default Address, then Employee person, then Home, then Work
                personLocationList.add(extractLocationFromAddress(locationData.employeeAddressHomeDefault()));
                personLocationList.add(extractLocationFromAddress(locationData.employeeAddressWorkDefault()));
                personLocationList.add(extractLocationFromAddress(locationData.erpUserAddressHomeDefault()));
                personLocationList.add(extractLocationFromAddress(locationData.erpUserAddressWorkDefault()));
                personLocationList.add(extractLocationFromAddress(locationData.employeeAddressHome()));
                personLocationList.add(extractLocationFromAddress(locationData.employeeAddressWork()));
                personLocationList.add(extractLocationFromAddress(locationData.erpUserAddressHome()));
                personLocationList.add(extractLocationFromAddress(locationData.erpUserAddressWork()));

                Location homeLocation;
                var index = 0;
                do {
                    homeLocation = personLocationList.get(index++);
                } while (homeLocation == null && index < personLocationList.size());

                locations.put(UuidMapper.toUUID(person.id()), homeLocation);
            });
        return locations;
    }

    private Location extractLocationFromAddress(LocationQueryApiDto.AddressDto address) {
        if (address == null || address.location() == null) {
            return null;
        } else {
            return new Location(address.location().latitude(), address.location().longitude());
        }
    }
}
