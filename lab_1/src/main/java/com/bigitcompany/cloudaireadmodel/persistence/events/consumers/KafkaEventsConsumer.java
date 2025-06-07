package com.bigitcompany.cloudaireadmodel.persistence.events.consumers;

import com.sap.fsm.springboot.starter.events.domain.Event;
import com.sap.fsm.springboot.starter.events.infrastructure.event.direct.DirectDispatcherConfiguration;
import com.sap.fsm.springboot.starter.events.infrastructure.event.direct.DirectEventConsumerDispatcher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaEventsConsumer {

    private final DirectEventConsumerDispatcher directEventConsumerDispatcher;

    public KafkaEventsConsumer(@Qualifier("directDispatcher") DirectEventConsumerDispatcher directEventConsumerDispatcher) {
        this.directEventConsumerDispatcher = directEventConsumerDispatcher;
    }

    @KafkaListener(
            topics = {"technical-events-data-high", "technical-events-data-low"},
            containerFactory = DirectDispatcherConfiguration.MANUAL_CONTAINER_FACTORY,
            concurrency = "${cloud-ai-read-model.kafka.technical-events.concurrency}"
    )
    public void consumeTechnicalEvents(List<ConsumerRecord<String, ? extends Event>> consumerRecords,
                                       Acknowledgment acknowledgment,
                                       Consumer<?, ?> consumer) {
        /*
         * DirectEventConsumerDispatcher is used for consuming the event from kafka and processing it immediately
         * AsyncThreadPooledEventConsumerDispatcher is somehow used for storing the event for later processing
         */
        directEventConsumerDispatcher.dispatch(consumerRecords, acknowledgment, consumer);
    }

    @KafkaListener(
            topics = {"snapshot-events-cloud-ai-read-model"},
            containerFactory = DirectDispatcherConfiguration.MANUAL_CONTAINER_FACTORY,
            concurrency = "${cloud-ai-read-model.kafka.snapshot-events.concurrency}"
    )
    public void consumeSnapshotEvents(List<ConsumerRecord<String, ? extends Event>> consumerRecords,
                                       Acknowledgment acknowledgment,
                                       Consumer<?, ?> consumer) {
        /*
         * DirectEventConsumerDispatcher is used for consuming the event from kafka and processing it immediately
         * AsyncThreadPooledEventConsumerDispatcher is somehow used for storing the event for later processing
         */
        directEventConsumerDispatcher.dispatch(consumerRecords, acknowledgment, consumer);
    }
}
