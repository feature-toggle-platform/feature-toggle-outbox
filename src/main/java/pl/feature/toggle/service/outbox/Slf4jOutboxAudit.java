package pl.feature.toggle.service.outbox;

import lombok.extern.slf4j.Slf4j;
import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.OutboxAudit;

@Slf4j
class Slf4jOutboxAudit implements OutboxAudit {

    @Override
    public void attemptIncreased(final Outbox outbox) {
        log.warn("Outbox attempt increased: {}", outbox);
    }

    @Override
    public void attemptLimitReached(final Outbox outbox) {
        log.error("Outbox attempt limit reached: {}", outbox);
    }

    @Override
    public void logException(Exception e) {
        log.error("Exception occurred while reading outbox events", e);
    }

    @Override
    public <T extends IntegrationEvent> void publish(Outbox<T> outbox) {
        log.info("Publishing outbox event to Kafka: eventId={}", outbox.eventId());
    }

    @Override
    public <T extends IntegrationEvent> void published(Outbox<T> outbox) {
        log.info("Outbox event published: eventId={}", outbox.eventId());
    }
}
