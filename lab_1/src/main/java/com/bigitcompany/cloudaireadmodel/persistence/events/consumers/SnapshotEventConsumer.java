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
import com.sap.fsm.data.event.snapshot.ActivitySnapshotEvent;
import com.sap.fsm.data.event.snapshot.AddressSnapshotEvent;
import com.sap.fsm.data.event.snapshot.BusinessPartnerSnapshotEvent;
import com.sap.fsm.data.event.snapshot.EquipmentSnapshotEvent;
import com.sap.fsm.data.event.snapshot.PersonReservationSnapshotEvent;
import com.sap.fsm.data.event.snapshot.PersonSnapshotEvent;
import com.sap.fsm.data.event.snapshot.PersonWorkTimePatternSnapshotEvent;
import com.sap.fsm.data.event.snapshot.RequirementSnapshotEvent;
import com.sap.fsm.data.event.snapshot.ServiceCallSnapshotEvent;
import com.sap.fsm.data.event.snapshot.SkillSnapshotEvent;
import com.sap.fsm.data.event.snapshot.TagSnapshotEvent;
import com.sap.fsm.data.event.snapshot.UdfMetaSnapshotEvent;
import com.sap.fsm.data.event.snapshot.WorkTimePatternSnapshotEvent;
import com.sap.fsm.data.event.snapshot.WorkTimeSnapshotEvent;
import com.sap.fsm.springboot.starter.events.domain.EventConsumer;
import org.apache.avro.specific.SpecificRecordBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;

@Component
public class SnapshotEventConsumer extends AbstractEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SnapshotEventConsumer.class);

    public SnapshotEventConsumer(ActivityService activityService,
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
    @NewSpan("consume-activity-snapshot-event")
    public void consumeActivity(ActivitySnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, activityService, "activity", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-address-snapshot-event")
    public void consumeAddress(AddressSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, addressService, "address", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-servicecall-snapshot-event")
    public void consumeServiceCall(ServiceCallSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, serviceCallService, "servicecall", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-wtp-snapshot-event")
    public void consumeWorkTimePattern(WorkTimePatternSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, workTimePatternService, "wtp", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-personwtp-snapshot-event")
    public void consumePersonWorkTimePattern(PersonWorkTimePatternSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, personWorkTimePatternService, "personwtp", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-equipment-snapshot-event")
    public void consumeEquipment(EquipmentSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, equipmentService, "equipment", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-person-snapshot-event")
    public void consumePerson(PersonSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, personService, "person", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-tag-snapshot-event")
    public void consumeTag(TagSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, tagService, "tag", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-skill-snapshot-event")
    public void consumeSkill(SkillSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, skillService, "skill", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-worktime-snapshot-event")
    public void consumeWorkTime(WorkTimeSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, workTimeService, "worktime", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-person-reservation-snapshot-event")
    public void consumePersonReservation(PersonReservationSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, personReservationService, "person-reservation", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-requirement-snapshot-event")
    public void consumeRequirement(RequirementSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, requirementService, "requirement", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-udfmeta-snapshot-event")
    public void consumeUdfMeta(UdfMetaSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, udfMetaService, "udfmeta", event.getId());
        markProcessingCompleted();
    }

    @EventConsumer
    @NewSpan("consume-businesspartner-snapshot-event")
    public void consumeBusinessPartner(BusinessPartnerSnapshotEvent event) {
        markProcessingStart();
        List<SpecificRecordBase> dtos = new ArrayList<>(event.getData().getSnapshots());
        consume(dtos, businessPartnerService, "businesspartner", event.getId());
        markProcessingCompleted();
    }

    private void consume(
            List<SpecificRecordBase> records,
            RecordAccessService repository,
            String recordTypeName,
            String eventId
    ) {
        logger.info("Processing {} snapshot event with ID {}", recordTypeName, eventId);
        tracingService.tagOnCurrentSpan("event-id", eventId);
        tracingService.tagOnCurrentSpan("record-type", recordTypeName);
        tracingService.tagOnCurrentSpan("record-count", records.size());
        EventDataCrudProcessor.createAll(records, repository);
    }
}