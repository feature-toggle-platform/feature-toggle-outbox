package pl.feature.toggle.service.outbox;

import lombok.extern.slf4j.Slf4j;
import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.OutboxRepository;
import pl.feature.toggle.service.outbox.api.OutboxWriter;
import pl.feature.toggle.service.outbox.api.Payload;
import pl.feature.toggle.service.outbox.api.Type;

@Slf4j
record OutboxWriterService(
        OutboxRepository outboxRepository,
        OutboxProperties props,
        ApplicationInfoProvider applicationInfoProvider
) implements OutboxWriter {

    @Override
    public void write(IntegrationEvent event, String topic) {
        var type = Type.create(event.getClass().getName());
        var payload = Payload.create(event);
        var attempts = Attempts.zero(props.getAttemptLimit());
        var applicationName = applicationInfoProvider.applicationName();
        var outbox = Outbox.from(event.eventId(), type, payload, attempts, applicationName, topic);
        log.info("Writing outbox event[{}]: {}", outbox.eventId().id(), outbox);
        outboxRepository.save(outbox);
    }
}
