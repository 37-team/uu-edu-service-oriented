package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.MetadataDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.MetadataResponseDto;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.services.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;

@Tag(name = "Metadata API", description = "Used to retrieve metadata about the data stored and processed in read model application.")
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class MetadataApi {

    private static final String COLLECTION = "metadata";

    private final MetadataService service;


    public MetadataApi(MetadataService service) {
        this.service = service;
    }

    @GetMapping(value = COLLECTION, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns information about the objects stored in the read model for this tenant")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER')")
    @Parameter(name = "x-account-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Account identifier", required = true)
    @Parameter(name = "x-company-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Company identifier", required = true)
    public MetadataResponseDto getMetadata(@RequestHeader(name = "x-account-id") @NotNull Long accountId,
                                           @RequestHeader(name = "x-company-id") @NotNull Long companyId) {
        var tenant = new Tenant(accountId, companyId);
        var counts = service.getObjectCount(tenant);
        var metadata = new MetadataDto(tenant.getAccountId(), tenant.getCompanyId(), counts);
        return new MetadataResponseDto(metadata);
    }
}