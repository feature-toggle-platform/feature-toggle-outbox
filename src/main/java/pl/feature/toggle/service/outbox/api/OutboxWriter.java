package pl.feature.toggle.service.outbox.api;


import pl.feature.toggle.service.contracts.shared.IntegrationEvent;

public interface OutboxWriter {

    void write(IntegrationEvent event, String topic);

}
