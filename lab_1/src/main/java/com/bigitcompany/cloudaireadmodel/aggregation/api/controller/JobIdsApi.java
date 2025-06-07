package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.JobIdsFilterDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ApiResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.JobIdsPartitionResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.JobIdsFilterMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.JobIdsPartitionMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.JobProxyService;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.JobService;
import com.bigitcompany.cloudaireadmodel.aggregation.security.TenantAuthorizerService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ActiveTenantProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

import java.util.List;
import java.util.Map;

import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_ACCOUNT_ID;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_CLIENT_ID;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_CLIENT_VERSION;
import static com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext.HEADER_COMPANY_ID;

@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class JobIdsApi extends ApiAccessController {

    public static final String JOB_IDS_URL_NAME = "job-ids";

    private final JobService jobService;

    private final JobProxyService jobProxyService;

    @Autowired
    public JobIdsApi(JobService jobService,
                     TenantAuthorizerService tenantAuthorizerService,
                     ActiveTenantProvider tenantProvider,
                     JobProxyService jobProxyService) {
        super(tenantAuthorizerService, tenantProvider);
        this.jobService = jobService;
        this.jobProxyService = jobProxyService;
    }


    @PostMapping(value = JOB_IDS_URL_NAME + "/actions/partition", consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns a list of job ids after filtering them by requirement")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public ApiResponseDto<JobIdsPartitionResponseDto> getJobIds(@Valid @RequestBody JobIdsFilterDto requestFilterDto,
                                                                @RequestParam(name = "proxy", required = false, defaultValue = "false") boolean proxy,
                                                                @RequestHeader(name = HEADER_ACCOUNT_ID) @NotNull Long accountId,
                                                                @RequestHeader(name = HEADER_COMPANY_ID) @NotNull Long companyId,
                                                                @RequestHeader(name = HEADER_CLIENT_ID) String clientId,
                                                                @RequestHeader(name = HEADER_CLIENT_VERSION) String clientVersion) {

        var tenant = new Tenant(accountId, companyId);
        ensureCorrectApiAccess(tenant, proxy, clientId, clientVersion);

        if (CollectionUtils.isEmpty(requestFilterDto.ids())) {
            var message = "Mandatory activity ids are empty or not part of the request body. Please provide a list of activity ids.";

            throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
        }

        if (CollectionUtils.isEmpty(requestFilterDto.requirementNames())) {
            var message = "Mandatory requirements are empty or not part of the request body. Please provide at least one requirement.";

            throw new DomainResponseException(HttpStatus.BAD_REQUEST, message);
        }

        Map<String, List<String>> results;
        var filter = JobIdsFilterMapper.toJobsIdsFilter(requestFilterDto);
        if (proxy) {
            results = jobProxyService.filterAndPartitionJobIdsByRequirements(filter);
        } else {
            results = jobService.filterAndPartitionJobIdsByRequirements(filter);
        }

        return new ApiResponseDto<>(JobIdsPartitionMapper.fromMapToDtos(results));
    }
}
