package pl.feature.toggle.service.outbox.api;


import pl.feature.toggle.service.contracts.shared.IntegrationEvent;

public record Payload<T extends IntegrationEvent>(
        T value
) {

    public static <T extends IntegrationEvent> Payload<T> create(T payload) {
        return new Payload<>(payload);
    }

}
