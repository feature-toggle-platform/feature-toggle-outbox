package com.configly.outbox.api;

import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.Outbox;

public interface OutboxAudit {

    void attemptIncreased(Outbox outbox);

    void attemptLimitReached(Outbox outbox);

    void logException(Exception e);

    <T extends IntegrationEvent> void publish(Outbox<T> outbox);

    <T extends IntegrationEvent> void published(Outbox<T> outbox);
}
