package pl.feature.toggle.service.outbox;

import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.OutboxException;
import pl.feature.toggle.service.outbox.api.OutboxPublisher;
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
