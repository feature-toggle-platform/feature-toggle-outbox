package pl.feature.toggle.service.api;


import com.ftaas.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.Outbox;

public interface OutboxPublisher {

    <T extends IntegrationEvent> void publish(Outbox<T> outbox);

}
