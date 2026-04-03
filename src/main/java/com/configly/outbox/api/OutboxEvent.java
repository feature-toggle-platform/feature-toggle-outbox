package com.configly.outbox.api;

import com.configly.contracts.shared.IntegrationEvent;
import com.configly.contracts.topic.KafkaTopic;

public record OutboxEvent(
        DestinationKey destinationKey,
        IntegrationEvent event,
        KafkaTopic topic
) {

    public static OutboxEvent of(DestinationKey destinationKey, IntegrationEvent event, KafkaTopic topic) {
        return new OutboxEvent(destinationKey, event, topic);
    }

    public static OutboxEvent of(String destinationKey, IntegrationEvent event, KafkaTopic topic) {
        return new OutboxEvent(new DestinationKey(destinationKey), event, topic);
    }

    public static OutboxEvent generatedKey(IntegrationEvent integrationEvent, KafkaTopic topic) {
        return new OutboxEvent(DestinationKey.generate(), integrationEvent, topic);
    }

}
