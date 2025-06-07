package com.bigitcompany.cloudaireadmodel.persistence.events.consumers;

import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.ActivityService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.AddressService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.BusinessPartnerService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.EquipmentService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.PersonReservationService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.PersonService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.PersonWorkTimePatternService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.RecordAccessService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.RequirementService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.ServiceCallService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.SkillService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.TagService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.UdfMetaService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.WorkTimePatternService;
import com.bigitcompany.cloudaireadmodel.persistence.persistence.records.WorkTimeService;
import com.bigitcompany.cloudaireadmodel.persistence.domain.services.EventDataCrudProcessor;
import com.bigitcompany.cloudaireadmodel.common.tracing.TracingService;
import com.sap.fsm.data.event.technical.ActivityTechnicalEvent;
import com.sap.fsm.data.event.technical.AddressTechnicalEvent;
import com.sap.fsm.data.event.technical.BusinessPartnerTechnicalEvent;
import com.sap.fsm.data.event.technical.EquipmentTechnicalEvent;
import com.sap.fsm.data.event.technical.PersonReservationTechnicalEvent;
import com.sap.fsm.data.event.technical.PersonTechnicalEvent;
import com.sap.fsm.data.event.technical.PersonWorkTimePatternTechnicalEvent;
import com.sap.fsm.data.event.technical.RequirementTechnicalEvent;
import com.sap.fsm.data.event.technical.ServiceCallTechnicalEvent;
import com.sap.fsm.data.event.technical.SkillTechnicalEvent;
import com.sap.fsm.data.event.technical.TagTechnicalEvent;
import com.sap.fsm.data.event.technical.TechnicalEventChangeType;
import com.sap.fsm.data.event.technical.UdfMetaTechnicalEvent;
import com.sap.fsm.data.event.technical.WorkTimePatternTechnicalEvent;
import com.sap.fsm.data.event.technical.WorkTimeTechnicalEvent;
import com.sap.fsm.data.event.technical.data.ActivityData;
import com.sap.fsm.data.event.technical.data.AddressData;
import com.sap.fsm.data.event.technical.data.BusinessPartnerData;
import com.sap.fsm.data.event.technical.data.EquipmentData;
import com.sap.fsm.data.event.technical.data.PersonData;
import com.sap.fsm.data.event.technical.data.PersonReservationData;
import com.sap.fsm.data.event.technical.data.PersonWorkTimePatternData;
import com.sap.fsm.data.event.technical.data.RequirementData;
import com.sap.fsm.data.event.technical.data.ServiceCallData;
import com.sap.fsm.data.event.technical.data.SkillData;
import com.sap.fsm.data.event.technical.data.TagData;
import com.sap.fsm.data.event.technical.data.UdfMetaData;
import com.sap.fsm.data.event.technical.data.WorkTimeData;
import com.sap.fsm.data.event.technical.data.WorkTimePatternData;
import com.sap.fsm.springboot.starter.events.domain.EventConsumer;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TechnicalEventConsumer extends AbstractEventConsumer{

    private static final Logger logger = LoggerFactory.getLogger(TechnicalEventConsumer.class);

    public TechnicalEventConsumer(ActivityService activityService,
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
        super(activityService, addressService, serviceCallService, workTimePatternService, personWorkTimePatternService,
                equipmentService, personService, tagService, workTimeService, skillService, personReservationService, requirementService,
                udfMetaService, businessPartnerService, tracingService);
    }

    @EventConsumer
    @NewSpan("consume-activity-technical-event")
    public void consumeActivity(ActivityTechnicalEvent event) {
        markProcessingStart();
        logger.info("ActivityTechnicalEvent {}", event);
        ActivityData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), activityService, "activity");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-address-technical-event")
    public void consumeAddress(AddressTechnicalEvent event) {
        markProcessingStart();
        logger.info("AddressTechnicalEvent {}", event);
        AddressData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), addressService, "address");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-servicecall-technical-event")
    public void consumeServiceCall(ServiceCallTechnicalEvent event) {
        markProcessingStart();
        logger.info("ServiceCallTechnicalEvent {}", event);
        ServiceCallData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), serviceCallService, "servicecall");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-wtp-technical-event")
    public void consumeWorkTimePattern(WorkTimePatternTechnicalEvent event) {
        markProcessingStart();
        logger.info("WorkTimePatternTechnicalEvent {}", event);
        WorkTimePatternData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), workTimePatternService, "wtp");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-personwtp-technical-event")
    public void consumePersonWorkTimePattern(PersonWorkTimePatternTechnicalEvent event) {
        markProcessingStart();
        logger.info("PersonWorkTimePatternTechnicalEvent {}", event);
        PersonWorkTimePatternData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), personWorkTimePatternService, "personwtp");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-equipment-technical-event")
    public void consumeEquipment(EquipmentTechnicalEvent event) {
        markProcessingStart();
        logger.info("EquipmentTechnicalEvent {}", event);
        EquipmentData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), equipmentService, "equipment");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-person-technical-event")
    public void consumePerson(PersonTechnicalEvent event) {
        markProcessingStart();
        logger.info("PersonTechnicalEvent {}", event);
        PersonData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), personService, "person");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-tag-technical-event")
    public void consumeTag(TagTechnicalEvent event) {
        markProcessingStart();
        logger.info("TagTechnicalEvent {}", event);
        TagData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), tagService, "tag");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-skill-technical-event")
    public void consumeSkill(SkillTechnicalEvent event) {
        markProcessingStart();
        logger.info("SkillTechnicalEvent {}", event);
        SkillData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), skillService, "skill");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-worktime-technical-event")
    public void consumeWorkTime(WorkTimeTechnicalEvent event) {
        markProcessingStart();
        logger.info("WorkTimeTechnicalEvent {}", event);
        WorkTimeData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), workTimeService, "worktime");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-personreservation-technical-event")
    public void consumePersonReservation(PersonReservationTechnicalEvent event) {
        markProcessingStart();
        logger.info("PersonReservationTechnicalEvent {}", event);
        PersonReservationData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), personReservationService, "personreservation");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-requirement-technical-event")
    public void consumeRequirement(RequirementTechnicalEvent event) {
        markProcessingStart();
        logger.info("RequirementTechnicalEvent {}", event);
        RequirementData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), requirementService, "requirement");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-udfmeta-technical-event")
    public void consumeUdfMeta(UdfMetaTechnicalEvent event) {
        markProcessingStart();
        logger.info("UdfMetaTechnicalEvent {}", event);
        UdfMetaData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), udfMetaService, "udfmeta");
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-businesspartner-technical-event")
    public void consumeBusinessPartner(BusinessPartnerTechnicalEvent event) {
        markProcessingStart();
        logger.info("BusinessPartnerTechnicalEvent {}", event);
        BusinessPartnerData data = event.getData();
        consume(data.getObjectId(), data.getChangeType(), data.getBefore(), data.getAfter(), data.getChangedFields(), businessPartnerService,  "businesspartner");
        markProcessingCompleted();
    }


    private void consume(
            String id,
            TechnicalEventChangeType changeType,
            SpecificRecordBase before,
            SpecificRecordBase after,
            List<String> changedFields,
            RecordAccessService repository,
            String recordTypeName
    ) {
        tracingService.tagOnCurrentSpan("change-type", changeType.toString());
        tracingService.tagOnCurrentSpan("record-type", recordTypeName);
        tracingService.tagOnCurrentSpan("object-id", id);

        switch (changeType) {
            case CREATE:
                if (Objects.nonNull(before)) {
                    logger.warn("Expected 'before' to be null on 'CREATE', instead was {}", before);
                }
                EventDataCrudProcessor.create(after, repository);
                break;
            case DELETE:
                EventDataCrudProcessor.delete(id, repository);
                break;
            case UPDATE:
                if (EventDataCrudProcessor.isDelete(after, changedFields)) {
                    EventDataCrudProcessor.delete(id, repository);
                } else {
                    EventDataCrudProcessor.update(id, before, after, changedFields, repository);
                }
                break;
            default:
                var warnMessage = String.format("Unexpected TechnicalEventChangeType %s", changeType);
                throw new UnsupportedOperationException(warnMessage);
        }
    }
}