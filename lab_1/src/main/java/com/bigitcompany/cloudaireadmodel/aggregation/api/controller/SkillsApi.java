package com.bigitcompany.cloudaireadmodel.aggregation.api.controller;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ApiResponseDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ResourceSkillDto;
import com.bigitcompany.cloudaireadmodel.aggregation.api.mapper.ResourceSkillMapper;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.proxy.SkillProxyService;
import com.bigitcompany.cloudaireadmodel.common.domain.model.exception.DomainResponseException;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;
import com.sap.fsm.springboot.starter.common.infrastructure.context.RequestContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.bigitcompany.cloudaireadmodel.aggregation.api.controller.SkillsApi.SKILLS_URL_NAME;

// TODO FSMCPB-92261 remove this class and all tests
@Tag(name = "Skills API", description = "Skills API")
@RestController
@RequestMapping(path = SKILLS_URL_NAME)
@Validated
public class SkillsApi {

    public static final String SKILLS_URL_NAME = "/api/v1/skills";

    private final SkillProxyService skillProxyService;

    public SkillsApi(SkillProxyService skillProxyService) {
        this.skillProxyService = skillProxyService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Returns skills of the given resources by resourceIds")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'USER', 'CUSTOMER', 'ADMINISTRATOR', 'SUPERUSER', 'SERVICE')")
    public ApiResponseDto<ResourceSkillDto> getSkills(@RequestParam(name = "resourceIds") @Size(max = 100) List<String> resourceIds) {
        String accountName = RequestContext.getAccountName();
        String companyName = RequestContext.getCompanyName();
        if (accountName == null || companyName == null) {
            throw new DomainResponseException(HttpStatus.BAD_REQUEST, "Missing accountName and companyName request headers");
        }

        var skillsByPersonIds = skillProxyService.getSkillsByPersonIds(UuidMapper.toUUIDs(resourceIds));
        final List<ResourceSkillDto> resourceSkillDtoList = ResourceSkillMapper.toDtos(skillsByPersonIds);
        return new ApiResponseDto<>(resourceSkillDtoList);
    }
}
