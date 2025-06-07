package com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JobDto {

    private String id;

    private String serviceCallId;

    private ActivityDto activity;

    private Instant earliestStartDateTime;

    private Instant dueDateTime;

    private LocationDto location;

    // TODO: 91052 clean up code
    private Set<String> optionalSkills;

    // TODO: 91052 clean up code
    private Set<String> mandatorySkills;

    @Schema(implementation = RequirementDto.class)
    private List<RequirementDto> requirements;

    private Integer durationMinutes;

    @Schema(
        type = "object",
        example = """
            {
              "urgency": "1"
            }"""
    )
    private Map<String, String> udfValues;

    private List<String> resourceExcludeList;

    private ExecutionStageDto executionStage;

    @Schema(implementation = EquipmentDto.class)
    private EquipmentDto equipment;

    @Schema(implementation = BusinessPartnerDto.class)
    private BusinessPartnerDto businessPartner;

    private String syncStatus;

    private AssignmentDto currentAssignmentStatus;

    private String priority;

    @JsonCreator
    public JobDto(String id,
                  String serviceCallId,
                  ActivityDto activity,
                  Instant earliestStartDateTime,
                  Instant dueDateTime,
                  LocationDto location,
                  Set<String> optionalSkills,
                  Set<String> mandatorySkills,
                  List<RequirementDto> requirements,
                  Integer durationMinutes,
                  Map<String, String> udfValues,
                  List<String> resourceExcludeList,
                  ExecutionStageDto executionStage,
                  EquipmentDto equipmentDto,
                  BusinessPartnerDto businessPartner,
                  String syncStatus,
                  AssignmentDto currentAssignmentStatus,
                  String priority
    ) {
        this.id = id;
        this.serviceCallId = serviceCallId;
        this.activity = activity;
        this.earliestStartDateTime = earliestStartDateTime;
        this.dueDateTime = dueDateTime;
        this.location = location;
        this.optionalSkills = optionalSkills;
        this.mandatorySkills = mandatorySkills;
        this.requirements = requirements;
        this.durationMinutes = durationMinutes;
        this.udfValues = udfValues;
        this.resourceExcludeList = resourceExcludeList;
        this.executionStage = executionStage;
        this.equipment = equipmentDto;
        this.businessPartner = businessPartner;
        this.syncStatus = syncStatus;
        this.currentAssignmentStatus = currentAssignmentStatus;
        this.priority = priority;
    }

    public JobDto() {
    }

    public String getId() {
        return id;
    }

    public String getServiceCallId() {
        return serviceCallId;
    }

    public ActivityDto getActivity() {
        return activity;
    }

    public Instant getEarliestStartDateTime() {
        return earliestStartDateTime;
    }

    public Instant getDueDateTime() {
        return dueDateTime;
    }

    public LocationDto getLocation() {
        return location;
    }

    public Set<String> getOptionalSkills() {
        return optionalSkills;
    }

    public Set<String> getMandatorySkills() {
        return mandatorySkills;
    }

    public List<RequirementDto> getRequirements() {
        return requirements;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Map<String, String> getUdfValues() {
        return udfValues;
    }

    public List<String> getResourceExcludeList() {
        return resourceExcludeList;
    }

    public ExecutionStageDto getExecutionStage() {
        return executionStage;
    }

    public EquipmentDto getEquipment() {
        return equipment;
    }

    public BusinessPartnerDto getBusinessPartner() {
        return businessPartner;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public AssignmentDto getCurrentAssignmentStatus() {
        return currentAssignmentStatus;
    }

    public String getPriority() {
        return priority;
    }
}
