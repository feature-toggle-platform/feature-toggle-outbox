package pl.feature.toggle.service.api;


import pl.feature.toggle.service.shared.IntegrationEvent;

public record Payload<T extends IntegrationEvent>(
        T value
) {

    public static <T extends IntegrationEvent> Payload<T> create(T payload) {
        return new Payload<>(payload);
    }

}
