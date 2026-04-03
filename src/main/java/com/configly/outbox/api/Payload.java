package com.configly.outbox.api;


import com.configly.contracts.shared.IntegrationEvent;

public record Payload<T extends IntegrationEvent>(
        T value
) {

    public static <T extends IntegrationEvent> Payload<T> create(T payload) {
        return new Payload<>(payload);
    }

}
