package pl.feature.toggle.service.outbox;

import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.OutboxAudit;

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
