package pl.feature.toggle.service.api;


import pl.feature.toggle.service.Outbox;
import pl.feature.toggle.service.shared.IntegrationEvent;

public interface OutboxPublisher {

    <T extends IntegrationEvent> void publish(Outbox<T> outbox);

}
