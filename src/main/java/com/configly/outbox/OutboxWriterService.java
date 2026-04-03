package com.configly.outbox;

import lombok.extern.slf4j.Slf4j;
import com.configly.outbox.api.*;

@Slf4j
record OutboxWriterService(
        OutboxRepository outboxRepository,
        OutboxProperties props,
        ApplicationInfoProvider applicationInfoProvider
) implements OutboxWriter {

    @Override
    public void write(OutboxEvent outboxEvent) {
        var type = Type.create(outboxEvent.event().getClass().getName());
        var payload = Payload.create(outboxEvent.event());
        var attempts = Attempts.zero(props.getAttemptLimit());
        var applicationName = applicationInfoProvider.applicationName();
        var outbox = Outbox.from(outboxEvent.destinationKey(), outboxEvent.event().eventId(), type, payload, attempts, applicationName, outboxEvent.topic().topicName());
        log.info("Writing outbox event[{}]: {}", outbox.eventId().id(), outbox);
        outboxRepository.save(outbox);
    }
}
