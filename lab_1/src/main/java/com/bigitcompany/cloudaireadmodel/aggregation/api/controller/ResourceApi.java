package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ResourceRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ApiResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ResourceResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.FetchMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.ResourceMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Resource;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.ResourceProxyService;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.ResourceService;
import com.bigitcompany.cloudaireadmodel.aggregation.security.TenantAuthorizerService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_ACCOUNT_ID;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_COMPANY_ID;

@Tag(name = "Resources API", description = "Resources API")
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class ResourceApi extends ApiAccessController {
    public static final String RESOURCE_URL_NAME = "resources";

    public static final int RESOURCE_IDS_SIZE_LIMIT = 100;

    public static final long MAX_BOOKING_FILTER_DURATION_DAYS = 365L;

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ResourceService resourceService;

    private final ResourceProxyService resourceProxyService;

    public ResourceApi(ResourceService resourceService, ResourceProxyService resourceProxyService, TenantAuthorizerService tenantAuthorizerService, ActiveTenantProvider tenantProvider) {
        super(tenantAuthorizerService, tenantProvider);
        this.resourceService = resourceService;
        this.resourceProxyService = resourceProxyService;
    }

    @PostMapping(value = RESOURCE_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns a list of resources with the provided personIDs and companyNames")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public ApiResponseDto<ResourceResponseDto> getResources(@Valid @RequestBody ResourceRequestDto resourceRequestDto,
                                                            @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxy,
                                                            @RequestHeader(name = HEADER_ACCOUNT_ID) @NotNull Long accountId,
                                                            @RequestHeader(name = HEADER_COMPANY_ID) @NotNull Long companyId,
                                                            @RequestHeader(name = RequestContext.HEADER_CLIENT_ID) String clientId,
                                                            @RequestHeader(name = RequestContext.HEADER_CLIENT_VERSION) String clientVersion) {
        // authorization checks
        var tenant = new Tenant(accountId, companyId);
        ensureCorrectApiAccess(tenant, proxy, clientId, clientVersion);

        // input validation
        var from = resourceRequestDto.getBookingsFilter().getEarliest();
        var to = resourceRequestDto.getBookingsFilter().getLatest();
        var optionsDto = resourceRequestDto.getOptions();

        if (from != null && to != null && ChronoUnit.DAYS.between(from, to) > MAX_BOOKING_FILTER_DURATION_DAYS) {
            // TODO FSMCPB-143242: Remove warning log and throw exception once no warning logs in prod system
            LOG.warn("Customer {} retrieving bookings between {} and {}, range should be less than {} days", tenant.getSchema(), from, to, MAX_BOOKING_FILTER_DURATION_DAYS);
        }
        if (optionsDto != null && optionsDto.isIncludeCrowdPersons() != null && optionsDto.isIncludeInternalPersons() != null &&
            !(optionsDto.isIncludeCrowdPersons() || optionsDto.isIncludeInternalPersons())) {
            var message = "'includeInternalPersons' and 'includeCrowdPersons' cannot be both false at the same time.";
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
        }
        if (CollectionUtils.isEmpty(resourceRequestDto.getPersonIds())) {
            var message = "No resource IDs provided for resource fetching";
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
        }

        // mapping
        var options = ResourceMapper.toResourceOptions(resourceRequestDto.getOptions());
        var bookingFilter = ResourceMapper.toBookingsFilter(resourceRequestDto.getBookingsFilter());
        var fetchUdfRequest = FetchMapper.toFetchUdfRequest(resourceRequestDto.getFetch());
        var fetchSkillRequest = FetchMapper.toFetchSkillRequest(resourceRequestDto.getFetch());

        List<UUID> personIds;
        try {
            personIds = UuidMapper.toUUIDs(resourceRequestDto.getPersonIds(), true);
        } catch (IllegalArgumentException e) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        List<Resource> results;
        if (proxy) {
            results = resourceProxyService.getResources(personIds, options, bookingFilter, fetchUdfRequest, fetchSkillRequest, RESOURCE_IDS_SIZE_LIMIT);
        } else {
            //TODO FSMCPB-141089: move this check to cover also proxy case
            if (resourceRequestDto.getPersonIds().size() > RESOURCE_IDS_SIZE_LIMIT) {
                var message = String.format("Number of resources [%d] requested for resource fetching exceeds maximum allowed [%d]", resourceRequestDto.getPersonIds().size(), RESOURCE_IDS_SIZE_LIMIT);
                throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
            }
            results = resourceService.getResources(personIds, options, bookingFilter, fetchUdfRequest, fetchSkillRequest);
        }

        return new ApiResponseDto<>(results.stream().map(ResourceMapper::toValidResourcesDto).toList());
    }
}
