package pl.feature.toggle.service.outbox;

import pl.feature.toggle.service.outbox.api.OutboxAudit;
import lombok.extern.slf4j.Slf4j;

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
    public void startReading(final int size) {
        log.info("Start reading outbox events: {}", size);
    }

    @Override
    public void logException(Exception e) {
        log.error("Exception occurred while reading outbox events", e);
    }
}
