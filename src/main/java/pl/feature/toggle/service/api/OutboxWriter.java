package pl.feature.toggle.service.api;

import com.ftaas.contracts.shared.IntegrationEvent;

public interface OutboxWriter {

    void write(IntegrationEvent event, String topic);

}
