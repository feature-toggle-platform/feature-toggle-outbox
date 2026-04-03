package com.configly.outbox;

import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.api.OutboxException;
import com.configly.outbox.api.OutboxPublisher;
import org.springframework.kafka.core.KafkaTemplate;

record OutboxKafkaPublisher(
        KafkaTemplate<String, Object> kafkaTemplate
) implements OutboxPublisher {

    @Override
    public <T extends IntegrationEvent> void publish(final Outbox<T> outbox) {
        try {
            kafkaTemplate.send(outbox.topic(), outbox.destinationKey().value(), outbox.payload().value())
                    .get();
        } catch (Exception e) {
            throw new OutboxException("Sending kafka message: " + outbox.eventId().toString() + " failed. :", e);
        }
    }
}
