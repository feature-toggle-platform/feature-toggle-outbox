package com.configly.outbox.api;


import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.Outbox;

public interface OutboxPublisher {

    <T extends IntegrationEvent> void publish(Outbox<T> outbox);

}
