package pl.feature.toggle.service.outbox;


import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.DestinationKey;
import pl.feature.toggle.service.outbox.api.Payload;
import pl.feature.toggle.service.outbox.api.Type;

import java.time.LocalDateTime;

public record Outbox<T extends IntegrationEvent>(
        DestinationKey destinationKey,
        EventId eventId,
        Status status,
        Attempts attempts,
        Payload<T> payload,
        Type type,
        LocalDateTime occurredAt,
        String applicationName,
        String topic
) {

    static <T extends IntegrationEvent> Outbox<T> from(DestinationKey destinationKey, EventId eventId, Type type, Payload<T> payload, Attempts attempts, String applicationName, String topic) {
        return new Outbox<>(
                destinationKey,
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
            return new Outbox<>(destinationKey, eventId, Status.createFailed(), newAttempts, payload, type, occurredAt, applicationName, topic);
        }
        return new Outbox<>(destinationKey, eventId, Status.createNew(), newAttempts, payload, type, occurredAt, applicationName, topic);
    }

    boolean isFailed() {
        return status.isFailed();
    }

    Outbox<T> published() {
        return withStatus(Status.createPublished());
    }

    private Outbox<T> withStatus(Status status) {
        return new Outbox<>(destinationKey, eventId, status, attempts, payload, type, occurredAt, applicationName, topic);
    }


    @Override
    public String toString() {
        return "Outbox: [" + eventId.id() + "] DestinationKey: [" +
                destinationKey.value() + "] Attempt: [" + attempts.attempt() + "] " +
                "Type: [" + type.value() + "] Status: [" + status.rawStatus() + "] Occurred at: ["
                + occurredAt + "] Producer: [" + applicationName + "]"
                + " Topic: [" + topic + "]";
    }
}
