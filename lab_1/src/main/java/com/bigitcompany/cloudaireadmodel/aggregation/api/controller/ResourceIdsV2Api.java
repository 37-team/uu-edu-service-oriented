package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourceIdsFilterDtoV2;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourcePartitionRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ApiResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ResourcePartitionDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.ResourceIdsFilterMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.ResourceProxyService;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.ResourceService;
import com.bigitcompany.cloudaireadmodel.aggregation.security.TenantAuthorizerService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_ACCOUNT_ID;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_ACCOUNT_NAME;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_CLIENT_ID;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_CLIENT_VERSION;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_COMPANY_ID;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_COMPANY_NAME;

@Tag(name = "Resources Ids API", description = "Resources Ids API")
@RestController
@RequestMapping(path = "/api/v2")
@Validated
public class ResourceIdsV2Api extends ApiAccessController {

    public static final String RESOURCE_IDS_V2_URL_NAME = "resource-ids";

    private final ResourceService resourceService;

    private final ResourceProxyService resourceProxyService;

    public ResourceIdsV2Api(ResourceService resourceService,
                            TenantAuthorizerService tenantAuthorizerService,
                            ActiveTenantProvider tenantProvider,
                            ResourceProxyService resourceProxyService) {
        super(tenantAuthorizerService, tenantProvider);
        this.resourceService = resourceService;
        this.resourceProxyService = resourceProxyService;
    }


    @PostMapping(value = RESOURCE_IDS_V2_URL_NAME + "/actions/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns a subset of provided resource ids after filtering them by the given request filter")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public ApiResponseDto<String> searchResourceIdsV2(@Valid @RequestBody ResourceIdsFilterDtoV2 requestFilterDto,
                                                    @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxy,
                                                    @RequestHeader(name = HEADER_ACCOUNT_ID) @NotNull Long accountId,
                                                    @RequestHeader(name = HEADER_COMPANY_ID) @NotNull Long companyId,
                                                    @RequestHeader(name = HEADER_ACCOUNT_NAME, required = false) String accountName,
                                                    @RequestHeader(name = HEADER_COMPANY_NAME, required = false) String companyName,
                                                    @RequestHeader(name = HEADER_CLIENT_ID) String clientId,
                                                    @RequestHeader(name = HEADER_CLIENT_VERSION) String clientVersion) {

        var tenant = new Tenant(accountId, accountName, companyId, companyName);
        ensureCorrectApiAccess(tenant, proxy, clientId, clientVersion);

        if (requestFilterDto != null && !(requestFilterDto.includeCrowdPersons() || requestFilterDto.includeInternalPersons())) {
            var message = "'includeInternalPersons' and 'includeCrowdPersons' cannot be both false at the same time.";
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
        }

        // Sanitize skills filter
        if (requestFilterDto != null && requestFilterDto.skills() != null) {
            requestFilterDto.skills().forEach(skill -> {
                if (skill.isBlank()) {
                    throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Empty skill filter is not allowed.");
                } else if (skill.contains("'")) {
                    throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Single quote is not allowed in skill filter.");
                } else if (skill.contains("\"")) {
                    throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Double quote is not allowed in skill filter.");
                } else if (skill.contains("\\")) {
                    throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Backslash is not allowed in skill filter.");
                } else if (skill.contains(";")) {
                    throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Semicolon is not allowed in skill filter.");
                }
            });
        }

        List<UUID> results;
        var filter = ResourceIdsFilterMapper.toResourceIdsFilterV2(requestFilterDto);
        if (proxy) {
            results = resourceProxyService.filterResourceIds(filter);
        } else {
            results = resourceService.filterResourceIds(filter);
        }
        return new ApiResponseDto<>(results.stream().map(UuidMapper::toFsmId).toList());
    }

    @PostMapping(value = RESOURCE_IDS_V2_URL_NAME + "/actions/partition", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "The request has been forwarded, processed, aggregated and returned. Response contains a list of partitions wrapped under the {results} array.")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public ApiResponseDto<ResourcePartitionDto> partition(
            @Valid @RequestBody(required = false) ResourcePartitionRequestDto resourcePartitionRequestDto,
            @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxyMode,
            @RequestHeader(name = HEADER_ACCOUNT_ID) @NotNull Long accountId,
            @RequestHeader(name = HEADER_COMPANY_ID) @NotNull Long companyId,
            @RequestHeader(name = HEADER_ACCOUNT_NAME, required = false) String accountName,
            @RequestHeader(name = HEADER_COMPANY_NAME, required = false) String companyName,
            @RequestHeader(name = HEADER_CLIENT_ID) String clientId,
            @RequestHeader(name = HEADER_CLIENT_VERSION) String clientVersion
    ) {
        var tenant = new Tenant(accountId, accountName, companyId, companyName);
        ensureCorrectApiAccess(tenant, proxyMode, clientId, clientVersion);
        Map<String, List<String>> skillsToPersonIdsMap;
        if (proxyMode) {
            skillsToPersonIdsMap = resourceProxyService.getPartitions(resourcePartitionRequestDto);
        } else {
            skillsToPersonIdsMap = resourceService.getPartitions(resourcePartitionRequestDto);
        }
        var results = skillsToPersonIdsMap.entrySet().stream().map(it -> new ResourcePartitionDto(it.getKey(), it.getValue())).toList();
        return new ApiResponseDto<>(results);
    }
}
