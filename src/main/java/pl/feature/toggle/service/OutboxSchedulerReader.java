package pl.feature.toggle.service;

import com.ftaas.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.api.OutboxAudit;
import pl.feature.toggle.service.api.OutboxPublisher;
import pl.feature.toggle.service.api.OutboxReader;
import pl.feature.toggle.service.api.OutboxRepository;
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
        audit.startReading(unprocessedOutboxes.size());

        for (var outbox : unprocessedOutboxes) {
            try {
                publish(outbox);
            } catch (Exception e) {
                increaseAttempt(outbox, e);
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
        publisher.publish(outbox);
        var published = outbox.published();
        repository.update(published);
    }

}
