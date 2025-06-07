package com.bigitcompany.cloudaireadmodel.common.connectors.queryapi.businesspartner;

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
import static com.bigitcompany.cloudaireadmodel.common.domain.model.SupportedFsmDtos.BUSINESSPARTNER;

@Component
public class BusinessPartnerClient extends AbstractQueryApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final Set<SupportedFsmDtos> REQUEST_DTOS = Set.of(BUSINESSPARTNER);

    public BusinessPartnerClient(@Value("${service.query-api.host}") String queryApiHost,
                                 @Value("${service.query-api.page-size}") int queryApiPageSize) {
        super(queryApiHost, queryApiPageSize, LOG);
    }

    public Entity queryBusinessPartnerById(ReadModelRequestContext requestContext, String businessPartnerId) {
        var body = createQueryBody(businessPartnerId);
        var page = fetchFirstPage(requestContext, REQUEST_DTOS, body, BusinessPartnerQueryApiDto.class);
        return parseResponse(page, businessPartnerId);
    }

    private Map<String, String> createQueryBody(String businessPartnerId) {
        String sqlSelect = """
            SELECT\s
                businessPartner.id,\s
                businessPartner.externalId,\s
                businessPartner.udfValues\s
             FROM\s
                BusinessPartner businessPartner\s
             WHERE businessPartner.id = '%s'\s
            """.formatted(businessPartnerId);
        return Map.of("query", sqlSelect);
    }

    private Entity parseResponse(BusinessPartnerQueryApiDto businessPartnerQueryApiDto, String businessPartnerId) {
        return businessPartnerQueryApiDto.getData().stream()
            .findFirst()
            .map(businessPartnerData -> {
                var businessPartner = businessPartnerData.getBusinessPartner();
                return new Entity(
                    UuidMapper.toUUID(businessPartner.id()),
                    businessPartner.externalId(),
                    udfValuesToMap(businessPartner.udfValues()));
            })
            .orElseThrow(() -> new DomainResponseException(HttpStatus.NOT_FOUND, String.format("Business partner with provided ID %s does not exists", businessPartnerId)));
    }
}