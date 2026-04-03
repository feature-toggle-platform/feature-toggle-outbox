package com.configly.outbox.api;


public interface OutboxWriter {

    void write(OutboxEvent event);

}
