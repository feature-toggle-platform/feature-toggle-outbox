package com.configly.outbox;

import com.configly.contracts.event.project.ProjectCreated;
import com.configly.contracts.shared.EventId;
import com.configly.outbox.api.DestinationKey;
import com.configly.outbox.api.Payload;
import com.configly.outbox.api.Type;

import java.time.LocalDateTime;
import java.util.UUID;

class FakeOutboxBuilder {

    private DestinationKey destinationKey;
    private EventId eventId;
    private Status status;
    private Attempts attempts;
    private Payload payload;
    private Type type;
    private LocalDateTime occurredAt;
    private String applicationName;
    private String topic;

    public static FakeOutboxBuilder builder() {
        return new FakeOutboxBuilder();
    }

    private FakeOutboxBuilder() {
        this.destinationKey = DestinationKey.generate();
        this.eventId = EventId.create();
        this.status = Status.createNew();
        this.attempts = Attempts.zero(2);
        this.payload = Payload.create(ProjectCreated.projectCreatedEventBuilder()
                .projectName("project name")
                .projectId(UUID.randomUUID())
                .build());
        this.occurredAt = LocalDateTime.now();
        this.type = Type.create(ProjectCreated.EVENT_TYPE);
        this.applicationName = "test";
        this.topic = "test";
    }


    public FakeOutboxBuilder withEventId(EventId eventId) {
        this.eventId = eventId;
        return this;
    }


    public FakeOutboxBuilder withStatus(Status status) {
        this.status = status;
        return this;
    }

    public FakeOutboxBuilder withAttempts(Attempts attempts) {
        this.attempts = attempts;
        return this;
    }

    public FakeOutboxBuilder withPayload(Payload payload) {
        this.payload = payload;
        return this;
    }

    public FakeOutboxBuilder withType(Type type) {
        this.type = type;
        return this;
    }

    public FakeOutboxBuilder withOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
        return this;
    }

    public FakeOutboxBuilder withApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public FakeOutboxBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public FakeOutboxBuilder withDestinationKey(DestinationKey destinationKey) {
        this.destinationKey = destinationKey;
        return this;
    }

    public Outbox build() {
        return new Outbox(
                destinationKey,
                eventId,
                status,
                attempts,
                payload,
                type,
                occurredAt,
                applicationName,
                topic
        );
    }

}
