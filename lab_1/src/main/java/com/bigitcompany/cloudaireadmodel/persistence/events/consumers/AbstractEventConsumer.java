package com.bigitcompany.cloudaireadmodel.persistence.events.consumers;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.ActivityService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.AddressService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.BusinessPartnerService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.EquipmentService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.PersonReservationService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.PersonService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.PersonWorkTimePatternService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.RequirementService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.ServiceCallService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.SkillService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.TagService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.UdfMetaService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.WorkTimePatternService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.WorkTimeService;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;

public abstract class AbstractEventConsumer {

    protected final ActivityService activityService;

    protected final AddressService addressService;

    protected final ServiceCallService serviceCallService;

    protected final WorkTimePatternService workTimePatternService;

    protected final PersonWorkTimePatternService personWorkTimePatternService;

    protected final EquipmentService equipmentService;

    protected final PersonService personService;

    protected final TagService tagService;

    protected final WorkTimeService workTimeService;

    protected final SkillService skillService;

    protected final PersonReservationService personReservationService;

    protected final RequirementService requirementService;

    protected final UdfMetaService udfMetaService;

    protected final BusinessPartnerService businessPartnerService;

    protected final TracingService tracingService;

    protected AbstractEventConsumer(ActivityService activityService,
                                    AddressService addressService,
                                    ServiceCallService serviceCallService,
                                    WorkTimePatternService workTimePatternService,
                                    PersonWorkTimePatternService personWorkTimePatternService,
                                    EquipmentService equipmentService,
                                    PersonService personService,
                                    TagService tagService,
                                    WorkTimeService workTimeService,
                                    SkillService skillService,
                                    PersonReservationService personReservationService,
                                    RequirementService requirementService,
                                    UdfMetaService udfMetaService,
                                    BusinessPartnerService businessPartnerService,
                                    TracingService tracingService) {
        this.activityService = activityService;
        this.addressService = addressService;
        this.serviceCallService = serviceCallService;
        this.workTimePatternService = workTimePatternService;
        this.personWorkTimePatternService = personWorkTimePatternService;
        this.equipmentService = equipmentService;
        this.personService = personService;
        this.tagService = tagService;
        this.workTimeService = workTimeService;
        this.skillService = skillService;
        this.personReservationService = personReservationService;
        this.requirementService = requirementService;
        this.udfMetaService = udfMetaService;
        this.businessPartnerService = businessPartnerService;
        this.tracingService = tracingService;
    }

    protected void markProcessingStart() {
        tracingService.eventOnCurrentSpan("started");
    }

    protected void markProcessingCompleted() {
        tracingService.eventOnCurrentSpan("finished");
    }
}