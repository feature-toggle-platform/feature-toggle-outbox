package com.configly.outbox;

import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.api.OutboxAudit;

class FakeAuditOutbox implements OutboxAudit {
    @Override
    public void attemptIncreased(final Outbox outbox) {

    }

    @Override
    public void attemptLimitReached(final Outbox outbox) {

    }

    @Override
    public void logException(Exception e) {

    }

    @Override
    public <T extends IntegrationEvent> void publish(Outbox<T> outbox) {

    }

    @Override
    public <T extends IntegrationEvent> void published(Outbox<T> outbox) {

    }
}
