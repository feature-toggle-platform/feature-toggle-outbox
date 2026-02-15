package pl.feature.toggle.service.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.jooq.JSON;
import pl.feature.ftaas.outbox.jooq.tables.records.OutboxEventsRecord;
import pl.feature.toggle.service.contracts.shared.EventId;
import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.Payload;
import pl.feature.toggle.service.outbox.api.Type;

import java.time.OffsetDateTime;

import static org.jooq.JSON.json;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
final class OutboxMapper {

    private final ObjectMapper objectMapper;


    public <T extends IntegrationEvent> Outbox<T> toDomain(OutboxEventsRecord record) {
        return new Outbox<>(
                EventId.of(record.getEventId()),
                new Status(Status.Outbox.valueOf(record.getStatus())),
                new Attempts(record.getAttempts(), record.getMaxAttempts(), record.getErrorMsg()),
                payload(record.getPayloadJson().data(), record.getType()),
                new Type(record.getType()),
                record.getOccurredAt().toLocalDateTime(),
                record.getProducer(),
                record.getTopic()
        );
    }

    public <T extends IntegrationEvent> OutboxEventsRecord fromDomain(Outbox<T> outbox) {
        final var record = new OutboxEventsRecord();
        record.setAttempts(outbox.attempts().attempt());
        record.setCreatedAt(OffsetDateTime.now());
        record.setType(outbox.type().value());
        record.setEventId(outbox.eventId().id());
        record.setPayloadJson(getJSON(outbox.payload()));
        record.setTopic(outbox.topic());
        record.setProducer(outbox.applicationName());
        record.setStatus(outbox.status().rawStatus());
        record.setOccurredAt(outbox.occurredAt().atOffset(OffsetDateTime.now().getOffset()));
        record.setMaxAttempts(outbox.attempts().limit());
        record.setErrorMsg(outbox.attempts().lastErrorMessage());
        return record;
    }

    private <T extends IntegrationEvent> Payload<T> payload(String jsonString, String type) {
        try {
            Class<?> raw = Class.forName(type);
            Class<T> clazz = (Class<T>) raw.asSubclass(IntegrationEvent.class);
            T value = objectMapper.readValue(jsonString, clazz);
            return Payload.create(value);
        } catch (ClassNotFoundException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends IntegrationEvent> JSON getJSON(Payload<T> payload) {
        try {
            String jsonString = objectMapper.writeValueAsString(payload.value());
            return json(jsonString);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
