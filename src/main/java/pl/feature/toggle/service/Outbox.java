package pl.feature.toggle.service;


import pl.feature.toggle.service.api.Payload;
import pl.feature.toggle.service.api.Type;
import pl.feature.toggle.service.shared.EventId;
import pl.feature.toggle.service.shared.IntegrationEvent;

import java.time.LocalDateTime;

public record Outbox<T extends IntegrationEvent>(
        EventId eventId,
        Status status,
        Attempts attempts,
        Payload<T> payload,
        Type type,
        LocalDateTime occurredAt,
        String applicationName,
        String topic
) {

    static <T extends IntegrationEvent> Outbox<T> from(EventId eventId, Type type, Payload<T> payload, Attempts attempts, String applicationName, String topic) {
        return new Outbox<>(
                eventId,
                Status.createNew(),
                attempts,
                payload,
                type,
                LocalDateTime.now(),
                applicationName,
                topic);
    }

    Outbox<T> increaseAttempt(String errorMessage) {
        var newAttempts = attempts.increase(errorMessage);
        if (newAttempts.limitReached()) {
            return new Outbox<>(eventId, Status.createFailed(), newAttempts, payload, type, occurredAt, applicationName, topic);
        }
        return new Outbox<>(eventId, Status.createNew(), newAttempts, payload, type, occurredAt, applicationName, topic);
    }

    boolean isFailed() {
        return status.isFailed();
    }

    Outbox<T> published() {
        return withStatus(Status.createPublished());
    }

    private Outbox<T> withStatus(Status status) {
        return new Outbox<>(eventId, status, attempts, payload, type, occurredAt, applicationName, topic);
    }


    @Override
    public String toString() {
        return "Outbox: [" + eventId.id() + "] Attempt: [" + attempts.attempt() + "] Type: [" + type.value() + "] Payload: [" + payload.value() + "] Status: [" + status.rawStatus() + "] Occurred at: [" + occurredAt + "] Producer: [" + applicationName + "]" + " Topic: [" + topic + "]";
    }
}
