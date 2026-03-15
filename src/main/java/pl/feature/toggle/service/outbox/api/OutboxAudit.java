package pl.feature.toggle.service.outbox.api;

import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.Outbox;

public interface OutboxAudit {

    void attemptIncreased(Outbox outbox);

    void attemptLimitReached(Outbox outbox);

    void logException(Exception e);

    <T extends IntegrationEvent> void publish(Outbox<T> outbox);

    <T extends IntegrationEvent> void published(Outbox<T> outbox);
}
