package com.bigitcompany.cloudaireadmodel.aggregation.api.mapper;

import com.bigitcompany.cloudaireadmodel.aggregation.api.dto.response.ActivityDto;
import com.bigitcompany.cloudaireadmodel.aggregation.domain.model.Activity;
import com.bigitcompany.cloudaireadmodel.common.domain.services.UuidMapper;

public class ActivityMapper {

    private ActivityMapper() {
    }

    public static ActivityDto toDto(Activity activity) {
        return new ActivityDto(
            UuidMapper.toFsmId(activity.getId()),
            activity.getExternalId(),
            activity.getLastChanged(),
            activity.getEarliestStartDateTime(),
            activity.getTravelTimeToInMinutes(),
            activity.getTravelTimeFromInMinutes(),
            activity.getPlannedDurationInMinutes()
        );
    }
}
