package com.bigitcompany.cloudaireadmodel.aggregation.domain.model;

import com.bigitcompany.cloudaireadmodel.common.domain.model.Location;
import com.bigitcompany.cloudaireadmodel.common.domain.model.annotations.Generated;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Job {

    private Activity activity;

    private List<Requirement> requirements;

    private List<UUID> resourceExcludeList;

    private Entity equipment;

    private Entity businessPartner;

    private ServiceCall serviceCall;

    private Location location;

    public Job(Activity activity) {
        this.requirements = new ArrayList<>();
        this.resourceExcludeList = new ArrayList<>();
        this.activity = activity;
    }

    public UUID getId() {
        return activity.getId();
    }

    public UUID getServiceCallId() {
        return serviceCall != null ? serviceCall.getId() : null;
    }

    public Assignment getCurrentAssignmentStatus() {
        if(activity.getResponsibles() == null || activity.getResponsibles().isEmpty()) {
            return null;
        }
        return new Assignment(activity.getResourceId(), activity.getStartDateTime(), activity.getEndDateTime());
    }

    public Activity getActivity() {
        return activity;
    }

    public Instant getEarliestStartDateTime() {
        return activity.getEarliestStartDateTime();
    }

    public Instant getDueDateTime() {
        return activity.getDueDateTime();
    }

    public Location getLocation() {
        return location;
    }

    public UUID getEquipmentId() {
        return equipment != null ? equipment.getId() : null;
    }

    public Entity getEquipment() {
        return equipment;
    }

    public UUID getBusinessPartnerId() {
        return businessPartner != null ? businessPartner.getId() : null;
    }

    public Entity getBusinessPartner() {
        return businessPartner;
    }

    // TODO: 91052 clean up code
    public Set<String> getOptionalRequirements() {
        return requirements
            .stream()
            .filter(req -> !req.mandatory())
            .map(Requirement::tag)
            .collect(Collectors.toUnmodifiableSet());
    }

    // TODO: 91052 clean up code
    public Set<String> getMandatoryRequirements() {
        return requirements
            .stream()
            .filter(Requirement::mandatory)
            .map(Requirement::tag)
            .collect(Collectors.toUnmodifiableSet());
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    public Integer getDurationMinutes() {
        if (activity.getDurationMinutes() != null) {
            return activity.getDurationMinutes();
        } else if (activity.getStartDateTime() != null && activity.getEndDateTime() != null) {
            long differentMinutes = Duration.between(activity.getStartDateTime(), activity.getEndDateTime()).toMinutes();

            // will throw an exception if job duration is greater than 1491308 days
            return Math.toIntExact(differentMinutes);
        }
        return null;
    }

    public Map<String, String> getUdfValues() {
        Map<String, String> udfValues = new HashMap<>();
        if(this.serviceCall != null && this.serviceCall.getUdfValues() != null) {
            udfValues.putAll(this.serviceCall.getUdfValues());
        }
        udfValues.putAll(this.activity.getUdfValues());
        return Collections.unmodifiableMap(udfValues);
    }

    public List<UUID> getResourcesToExclude() {
        return Collections.unmodifiableList(resourceExcludeList != null ? resourceExcludeList : Collections.emptyList());
    }

    public ExecutionStage getExecutionStage() {
        return activity.getExecutionStage();
    }

    public String getSyncStatus() {
        return activity.getSyncStatus();
    }

    public String getPriority() {
        if(serviceCall == null) {
            return null;
        }
        return serviceCall.getPriority();
    }

    //mutators
    public void addRequirements(List<Requirement> requirements) {
        if(requirements == null) {
            return;
        }
        this.requirements.addAll(requirements);
    }

    public void setEquipment(Entity equipment) {
        this.equipment = equipment;
    }

    public void setBusinessPartner(Entity businessPartner) {
        this.businessPartner = businessPartner;
    }

    public void setServiceCall(ServiceCall serviceCall) {
        this.serviceCall = serviceCall;
    }

    public void setResourceExcludeList(List<UUID> resourceExcludeList) {
        this.resourceExcludeList = resourceExcludeList;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Generated
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(activity, job.activity) && Objects.equals(requirements, job.requirements) && Objects.equals(resourceExcludeList, job.resourceExcludeList) && Objects.equals(equipment, job.equipment) && Objects.equals(businessPartner, job.businessPartner) && Objects.equals(serviceCall, job.serviceCall) && Objects.equals(location, job.location);
    }

    @Generated
    @Override
    public int hashCode() {
        return Objects.hash(activity, requirements, resourceExcludeList, equipment, businessPartner, serviceCall, location);
    }

    @Generated
    @Override
    public String toString() {
        return "Job{" +
                "activity=" + activity +
                ", requirements=" + requirements +
                ", resourceExcludeList=" + resourceExcludeList +
                ", equipment=" + equipment +
                ", businessPartner=" + businessPartner +
                ", serviceCall=" + serviceCall +
                ", location=" + location +
                '}';
    }
}
