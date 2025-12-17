package pl.feature.toggle.service.outbox.api;


import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.Outbox;

import java.util.List;

public interface OutboxRepository {

    <T extends IntegrationEvent> void save(Outbox<T> outbox);

    <T extends IntegrationEvent> List<Outbox<T>> findUnprocessedOutboxes(int limit);

    <T extends IntegrationEvent> void update(Outbox<T> outbox);

}
