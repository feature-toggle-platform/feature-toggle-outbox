package com.configly.outbox;

import org.slf4j.MDC;
import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.api.OutboxAudit;
import com.configly.outbox.api.OutboxPublisher;
import com.configly.outbox.api.OutboxReader;
import com.configly.outbox.api.OutboxRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
class OutboxSchedulerReader implements OutboxReader {

    private final OutboxRepository repository;
    private final OutboxProperties properties;
    private final OutboxPublisher publisher;
    private final OutboxAudit audit;


    @Scheduled(
            fixedDelayString = "${outbox.pool-interval-ms:10000}",
            initialDelayString = "${outbox.initial-delay-ms:0}"
    )
    @Transactional
    public void process() {
        final var unprocessedOutboxes = repository.findUnprocessedOutboxes(properties.getBatchSize());
        for (var outbox : unprocessedOutboxes) {
            try {
                MDC.put("correlationId", outbox.payload().value().correlationId());
                publish(outbox);
            } catch (Exception e) {
                increaseAttempt(outbox, e);
            } finally {
                MDC.clear();
            }
        }
    }

    private <T extends IntegrationEvent> void increaseAttempt(final Outbox<T> outbox, final Exception e) {
        audit.logException(e);
        var increased = outbox.increaseAttempt(ExceptionUtils.getStackTrace(e));
        if (increased.isFailed()) {
            audit.attemptLimitReached(increased);
            repository.update(increased);
            return;
        }
        audit.attemptIncreased(increased);
        repository.update(increased);
    }

    private <T extends IntegrationEvent> void publish(final Outbox<T> outbox) {
        audit.publish(outbox);
        publisher.publish(outbox);
        var published = outbox.published();
        repository.update(published);
        audit.published(outbox);
    }

}
