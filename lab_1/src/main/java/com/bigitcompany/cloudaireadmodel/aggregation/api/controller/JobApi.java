package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.JobRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ApiResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.JobResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.AdditionalDataOptionsMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.FetchMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.JobMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.ValidatedJobs;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.JobProxyService;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.JobService;
import com.bigitcompany.cloudaireadmodel.aggregation.security.TenantAuthorizerService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Tag(name = "Aggregate Jobs API", description = "Aggregate Jobs API")
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class JobApi extends ApiAccessController {

    private static final String JOB_URL_NAME = "jobs";

    public static final int JOB_IDS_SIZE_LIMIT = 4000;

    private final JobService jobService;

    private final JobProxyService jobProxyService;

    @Autowired
    public JobApi(JobService jobService, JobProxyService jobProxyService, TenantAuthorizerService tenantAuthorizerService, ActiveTenantProvider tenantProvider) {
        super(tenantAuthorizerService, tenantProvider);
        this.jobService = jobService;
        this.jobProxyService = jobProxyService;
    }

    @PostMapping(value = JOB_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns a list of jobs with the provided IDs, intended to be used for optimization")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public ApiResponseDto<JobResponseDto> getJobs(
        @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxy,
        @Valid @RequestBody JobRequestDto requestDto,
        @RequestHeader(name = "x-account-id") @NotNull Long accountId,
        @RequestHeader(name = "x-company-id") @NotNull Long companyId,
        @RequestHeader(name = "x-account-name", required = false) String accountName,
        @RequestHeader(name = "x-company-name", required = false) String companyName,
        @RequestHeader(name = RequestContext.HEADER_CLIENT_ID) String clientId,
        @RequestHeader(name = RequestContext.HEADER_CLIENT_VERSION) String clientVersion
    ) {

        var tenant = new Tenant(accountId, accountName, companyId, companyName);
        ensureCorrectApiAccess(tenant, proxy, clientId, clientVersion);

        var additionalDataOptionsRequest = AdditionalDataOptionsMapper.toAdditionalDataOptionsRequest(requestDto.getAdditionalDataOptions());
        var fetchUdfRequest = FetchMapper.toFetchUdfRequest(requestDto.getFetch());
        List<UUID> jobIds;
        try {
            if (CollectionUtils.isEmpty(requestDto.getJobIds())) {
                var message = "No job IDs provided for job fetching";

                throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
            }
            jobIds = UuidMapper.toUUIDs(requestDto.getJobIds(), true);
        } catch (IllegalArgumentException e) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        ValidatedJobs results;
        if (proxy) {
            results = jobProxyService.getJobs(jobIds, additionalDataOptionsRequest, fetchUdfRequest);
        } else {
            if (requestDto.getJobIds().size() > JOB_IDS_SIZE_LIMIT) {
                var message = String.format("Number of jobs [%d] requested for job fetching exceeds maximum allowed [%d]", requestDto.getJobIds().size(), JOB_IDS_SIZE_LIMIT);
                throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
            }

            results = jobService.getJobs(jobIds, additionalDataOptionsRequest, fetchUdfRequest);
        }

        var response = JobMapper.toJobResponseDTO(results);
        return new ApiResponseDto<>(List.of(response));
    }
}