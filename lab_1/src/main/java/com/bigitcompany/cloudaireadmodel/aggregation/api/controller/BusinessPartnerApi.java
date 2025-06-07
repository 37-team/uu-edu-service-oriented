package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.BusinessPartnerDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.BusinessPartnerMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.BusinessPartnerProxyService;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.BusinessPartnerAggregateService;
import com.bigitcompany.cloudaireadmodel.aggregation.security.TenantAuthorizerService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static com.bigitcompany.cloudaireadmodel.aggregation.api.controller.BusinessPartnerApi.BUSINESS_PARTNER_URL_NAME;

@Tag(name = "Business Partner API", description = "Business Partner API")
@RestController
@RequestMapping(path = BUSINESS_PARTNER_URL_NAME)
@Validated
public class BusinessPartnerApi extends ApiAccessController {

    public static final String BUSINESS_PARTNER_URL_NAME = "/api/v1/business-partner";

    private final BusinessPartnerAggregateService businessPartnerService;
    private final BusinessPartnerProxyService businessPartnerProxyService;

    public BusinessPartnerApi(TenantAuthorizerService tenantAuthorizerService, BusinessPartnerAggregateService businessPartnerService, BusinessPartnerProxyService businessPartnerProxyService, ActiveTenantProvider tenantProvider) {
        super(tenantAuthorizerService, tenantProvider);
        this.businessPartnerService = businessPartnerService;
        this.businessPartnerProxyService = businessPartnerProxyService;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns business partner by id")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public BusinessPartnerDto getBusinessPartner(@Parameter(required = true) @PathVariable @Valid @NotNull String id,
                                                 @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxy,
                                                 @RequestHeader(name = RequestContext.HEADER_CLIENT_ID) String clientId,
                                                 @RequestHeader(name = RequestContext.HEADER_CLIENT_VERSION) String clientVersion) {

        Long companyId = RequestContext.getCompanyId();
        String accountName = RequestContext.getAccountName();
        Long accountId = RequestContext.getAccountId();
        String companyName = RequestContext.getCompanyName();

        var tenant = new Tenant(accountId, accountName, companyId, companyName);
        ensureCorrectApiAccess(tenant, proxy, clientId, clientVersion);

        Entity businessPartner = proxy
            ? businessPartnerProxyService.getBusinessPartner(UuidMapper.toFsmId(id))
            : businessPartnerService.getBusinessPartner(UuidMapper.toUUID(id))
        ;

        return BusinessPartnerMapper.toDto(businessPartner);
    }
}