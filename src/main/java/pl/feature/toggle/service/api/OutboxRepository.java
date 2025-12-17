package pl.feature.toggle.service.api;


import com.ftaas.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.Outbox;

import java.util.List;

public interface OutboxRepository {

    <T extends IntegrationEvent> void save(Outbox<T> outbox);

    <T extends IntegrationEvent> List<Outbox<T>> findUnprocessedOutboxes(int limit);

    <T extends IntegrationEvent> void update(Outbox<T> outbox);

}
