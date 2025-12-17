package pl.feature.toggle.service.api;


import pl.feature.toggle.service.shared.IntegrationEvent;

public interface OutboxWriter {

    void write(IntegrationEvent event, String topic);

}
