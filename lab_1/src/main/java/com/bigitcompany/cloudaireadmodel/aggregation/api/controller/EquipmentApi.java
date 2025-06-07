package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.EquipmentDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.EquipmentMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Entity;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.EquipmentProxyService;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.EquipmentAggregateService;
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

import static com.bigitcompany.cloudaireadmodel.aggregation.api.controller.EquipmentApi.EQUIPMENT_URL_NAME;

@Tag(name = "Equipment API", description = "Equipment API")
@RestController
@RequestMapping(path = EQUIPMENT_URL_NAME)
@Validated
public class EquipmentApi extends ApiAccessController {

    public static final String EQUIPMENT_URL_NAME = "/api/v1/equipment";
    private final EquipmentAggregateService equipmentAggregateService;
    private final EquipmentProxyService equipmentProxyService;

    public EquipmentApi(TenantAuthorizerService tenantAuthorizerService, EquipmentAggregateService equipmentAggregateService, EquipmentProxyService equipmentProxyService, ActiveTenantProvider tenantProvider) {
        super(tenantAuthorizerService, tenantProvider);
        this.equipmentAggregateService = equipmentAggregateService;
        this.equipmentProxyService = equipmentProxyService;
    }
    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns equipment by id")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public EquipmentDto getEquipment(@Parameter(required = true) @PathVariable @Valid @NotNull String id,
                                     @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxy,
                                     @RequestHeader(name = RequestContext.HEADER_CLIENT_ID) String clientId,
                                     @RequestHeader(name = RequestContext.HEADER_CLIENT_VERSION) String clientVersion) {

        Long companyId = RequestContext.getCompanyId();
        String accountName = RequestContext.getAccountName();
        Long accountId = RequestContext.getAccountId();
        String companyName = RequestContext.getCompanyName();

        var tenant = new Tenant(accountId, accountName, companyId, companyName);
        ensureCorrectApiAccess(tenant, proxy, clientId, clientVersion);

        Entity equipment = proxy
            ? equipmentProxyService.getEquipment(UuidMapper.toFsmId(id))
            : equipmentAggregateService.getEquipment(UuidMapper.toUUID(id))
        ;

        return EquipmentMapper.toDto(equipment);
    }
}
