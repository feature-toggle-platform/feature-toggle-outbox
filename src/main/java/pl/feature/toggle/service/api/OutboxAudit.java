package pl.feature.toggle.service.api;

import pl.feature.toggle.service.Outbox;

public interface OutboxAudit {

    void attemptIncreased(Outbox outbox);

    void attemptLimitReached(Outbox outbox);

    void startReading(final int size);

    void logException(Exception e);
}
