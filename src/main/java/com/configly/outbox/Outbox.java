package com.configly.outbox;


import com.configly.contracts.shared.EventId;
import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.api.DestinationKey;
import com.configly.outbox.api.Payload;
import com.configly.outbox.api.Type;

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
