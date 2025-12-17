package pl.feature.toggle.service.outbox.api;


import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.Outbox;

public interface OutboxPublisher {

    <T extends IntegrationEvent> void publish(Outbox<T> outbox);

}
