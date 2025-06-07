package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.request.ConfigurationRequestDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ConfigurationResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.ConfigurationMapper;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Configuration;
import com.bigitcompany.cloudaireadmodel.common.domain.model.Tenant;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Configuration API", description = "Configuration API")
@RestController
@RequestMapping(path = "/api/v1/")
@Validated
public class ConfigurationApi {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String CONFIGURATION_URL_NAME = "configurations";

    private final ConfigurationService configurationService;

    public ConfigurationApi(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping(value = CONFIGURATION_URL_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a configuration for one tenant")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    @Parameter(name = "x-account-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Account identifier", required = true)
    @Parameter(name = "x-company-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Company identifier", required = true)
    public ConfigurationResponseDto getTenantConfiguration(@RequestHeader(name = "x-account-id") @NotNull Long accountId,
                                                           @RequestHeader(name = "x-company-id") @NotNull Long companyId) {
        var tenant = new Tenant(accountId, companyId);
        return ConfigurationMapper.toConfigurationResponseDto(configurationService.getTenantConfiguration(tenant)
            .orElseThrow(() -> {
                var message = String.format("Configuration for accountId %s and companyID %s  could not be found.", accountId, companyId);
                LOG.warn(message);
                return new DomainResponseException(HttpStatus.NOT_FOUND, message);
            }));
    }

    @PostMapping(value = CONFIGURATION_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Create an configuration for tenant")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER')")
    @Parameter(name = "x-account-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Account identifier", required = true)
    @Parameter(name = "x-company-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Company identifier", required = true)
    public ResponseEntity<UUID> createTenantConfiguration(@Parameter(required = true) @Valid @RequestBody ConfigurationRequestDto requestDto,
                                                          @RequestHeader(name = "x-account-id") @NotNull Long accountId,
                                                          @RequestHeader(name = "x-company-id") @NotNull Long companyId) {
        var tenant = new Tenant(accountId, companyId);
        var configuration = new Configuration(tenant, requestDto.isIndexingEnabled(), requestDto.isQueryingEnabled());

        Optional<Configuration> existingConfiguration = configurationService.getTenantConfiguration(tenant);
        if (existingConfiguration.isPresent()) {
            throw new DomainResponseException(HttpStatus.CONFLICT, "Configuration already exists");
        }
        var savedConfigurationId = configurationService.createTenantConfiguration(configuration);
        return configuration.isIndexingEnabled() ?
            new ResponseEntity<>(savedConfigurationId, HttpStatus.ACCEPTED) : // Signalling that an async snapshot event has been triggered
            new ResponseEntity<>(savedConfigurationId, HttpStatus.CREATED);
    }

    @PatchMapping(value = CONFIGURATION_URL_NAME, consumes = MediaType.APPLICATION_JSON_VALUE, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Update an configuration for tenant")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER')")
    @Parameter(name = "x-account-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Account identifier", required = true)
    @Parameter(name = "x-company-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Company identifier", required = true)
    public ResponseEntity<Void> updateTenantConfiguration(@Parameter(required = true) @Valid @RequestBody ConfigurationRequestDto requestDto,
                                                          @RequestHeader(name = "x-account-id") @NotNull Long accountId,
                                                          @RequestHeader(name = "x-company-id") @NotNull Long companyId) {
        var tenant = new Tenant(accountId, companyId);
        var existing = configurationService.getTenantConfiguration(tenant).orElseThrow(() -> {
            var message = String.format("Configuration for accountId %s and companyID %s could not be found. Please create one.", accountId, companyId);
            LOG.warn(message);
            return new DomainResponseException(HttpStatus.NOT_FOUND, message);
        });
        var update = configurationService.mergeConfigurationForUpdate(existing, requestDto.isIndexingEnabled(), requestDto.isQueryingEnabled());

        if (!update.isIndexingEnabled() && update.isQueryingEnabled()) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Cannot disable indexing while keeping querying enabled!");
        }

        configurationService.updateTenantConfiguration(update);
        return update.isIndexingEnabled() && !existing.isIndexingEnabled() ?
            new ResponseEntity<>(HttpStatus.ACCEPTED) : // Signalling that an async snapshot event has been triggered
            new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(CONFIGURATION_URL_NAME)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a tenant configuration")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER')")
    @Parameter(name = "x-account-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Account identifier", required = true)
    @Parameter(name = "x-company-id", schema = @Schema(type = "long"), in = ParameterIn.HEADER, description = "Tenant Company identifier", required = true)
    public void deleteConfiguration(@RequestHeader(name = "x-account-id") @NotNull Long accountId,
                                    @RequestHeader(name = "x-company-id") @NotNull Long companyId) {
        var tenant = new Tenant(accountId, companyId);
        Optional<Configuration> existingConfiguration = configurationService.getTenantConfiguration(tenant);
        existingConfiguration.ifPresent(configurationService::deleteConfiguration);
    }
}