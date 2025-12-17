package pl.feature.toggle.service;

import pl.feature.toggle.service.api.OutboxException;
import pl.feature.toggle.service.api.OutboxPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import pl.feature.toggle.service.shared.IntegrationEvent;

record OutboxKafkaPublisher(
        KafkaTemplate<String, Object> kafkaTemplate
) implements OutboxPublisher {

    @Override
    public <T extends IntegrationEvent> void publish(final Outbox<T> outbox) {
        final var id = outbox.eventId().toString();
        try {
            kafkaTemplate.send(outbox.topic(), id, outbox.payload().value())
                    .get();
        } catch (Exception e) {
            throw new OutboxException("Sending kafka message: " + id + " failed. :", e);
        }
    }
}
