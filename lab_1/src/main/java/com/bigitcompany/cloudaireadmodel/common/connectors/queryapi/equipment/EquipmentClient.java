package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.equipment;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.AbstractQueryApiClient;
import com.bigitcompany.cloudaireadmodel.common.domain.model.ReadModelRequestContext;
import com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Set;

import static com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.common.UdfValue.udfValuesToMap;
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.EQUIPMENT;

@Component
public class EquipmentClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(EQUIPMENT);

    public EquipmentClient(@Value("${service.query-api.host}") String queryApiHost,
                           @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    public Entity queryEquipmentById(ReadModelRequestContext requestContext, String id) {
        var body = createQueryBody(id);
        var page = fetchFirstPage(requestContext, REQUEST_DTOS, body, EquipmentQueryApiDto.class);
        return parseResponse(page, id);
    }

    private Map<String, String> createQueryBody(String id) {
        String sqlSelect = """
                SELECT
                    equipment.id,
                    equipment.externalId,
                    equipment.udfValues
                FROM
                    Equipment equipment
                WHERE equipment.id = '%s'
            """.formatted(id);
        return Map.of("query", sqlSelect);
    }

    private Entity parseResponse(EquipmentQueryApiDto equipmentQueryApiDto, String id) {
        return equipmentQueryApiDto.getData().stream()
            .findFirst()
            .map(equipmentData -> {
                var equipment = equipmentData.getEquipment();
                return new Entity(
                    UuidMapper.toUUID(equipment.id()),
                    equipment.externalId(),
                    udfValuesToMap(equipment.udfValues())
                );
            })
            .orElseThrow(() -> new DomainResponseException(HttpStatus.NOT_FOUND, String.format("Equipment with provided ID %s does not exists", id)));
    }
}