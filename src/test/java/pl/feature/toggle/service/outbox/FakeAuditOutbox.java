package pl.feature.toggle.service.outbox;

import pl.feature.toggle.service.outbox.api.OutboxAudit;

class FakeAuditOutbox implements OutboxAudit {
    @Override
    public void attemptIncreased(final Outbox outbox) {

    }

    @Override
    public void attemptLimitReached(final Outbox outbox) {

    }

    @Override
    public void startReading(final int size) {

    }

    @Override
    public void logException(Exception e) {

    }
}
