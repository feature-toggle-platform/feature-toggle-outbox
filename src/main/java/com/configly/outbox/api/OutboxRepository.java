package com.configly.outbox.api;


import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.Outbox;

import java.util.List;

public interface OutboxRepository {

    <T extends IntegrationEvent> void save(Outbox<T> outbox);

    <T extends IntegrationEvent> List<Outbox<T>> findUnprocessedOutboxes(int limit);

    <T extends IntegrationEvent> void update(Outbox<T> outbox);

}
