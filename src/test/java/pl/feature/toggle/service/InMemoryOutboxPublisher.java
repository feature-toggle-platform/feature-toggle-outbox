package pl.feature.toggle.service;

import pl.feature.toggle.service.api.OutboxPublisher;
import pl.feature.toggle.service.shared.EventId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class InMemoryOutboxPublisher implements OutboxPublisher {

    private final Map<EventId, Outbox> outboxMap = new HashMap<>();
    private boolean throwExceptionOnNextPublish = false;
    private String message;

    @Override
    public void publish(final Outbox outbox) {
        if (throwExceptionOnNextPublish) {
            throw new IllegalArgumentException(message);
        }
        outboxMap.put(outbox.eventId(), outbox);
    }

    void exceptionOnNextPublish(String message) {
        throwExceptionOnNextPublish = true;
        this.message = message;
    }

    List<Outbox> queuedOutboxes() {
        return List.copyOf(outboxMap.values());
    }

    void clear() {
        outboxMap.clear();
    }

    Outbox getOutbox(final EventId eventId) {
        return outboxMap.get(eventId);
    }

    boolean contains(final EventId eventId) {
        return outboxMap.containsKey(eventId);
    }


}
