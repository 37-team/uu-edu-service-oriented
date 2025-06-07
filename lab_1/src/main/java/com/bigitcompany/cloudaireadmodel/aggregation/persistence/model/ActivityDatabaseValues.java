package com.bigitcompany.cloudaireadmodel.aggregation.persistence.model;

import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Udf;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityDatabaseValues {

    private ActivityData activityData;

    private  Map<UUID, Map<String, String>> equipmentUdfMap;

    private  Map<UUID, Map<String, String>> businessPartnerUdfMap;

    private List<Udf> activityUDFs;

    private List<Udf> serviceCallUDFs;

    public ActivityDatabaseValues(ActivityData activityData,  Map<UUID, Map<String, String>> equipmentUdfMap,  Map<UUID, Map<String, String>> businessPartnerUdfMap, List<Udf> activityUDFs, List<Udf> serviceCallUDFs) {
        this.activityData = activityData;
        this.equipmentUdfMap = equipmentUdfMap;
        this.businessPartnerUdfMap = businessPartnerUdfMap;
        this.activityUDFs = activityUDFs;
        this.serviceCallUDFs = serviceCallUDFs;
    }

    public ActivityData getActivityData() {
        return activityData;
    }

    public  Map<UUID, Map<String, String>> getEquipmentUdfMap() {
        return equipmentUdfMap;
    }

    public  Map<UUID, Map<String, String>> getBusinessPartnerUdfMap() {
        return businessPartnerUdfMap;
    }

    public List<Udf> getActivityUDFs() {
        return activityUDFs;
    }

    public List<Udf> getServiceCallUDFs() {
        return serviceCallUDFs;
    }
}
