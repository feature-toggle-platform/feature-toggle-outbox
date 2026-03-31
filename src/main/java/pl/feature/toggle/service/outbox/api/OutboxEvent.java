package pl.feature.toggle.service.outbox.api;

import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.contracts.topic.KafkaTopic;

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

    public static OutboxEvent of(IntegrationEvent integrationEvent, KafkaTopic topic) {
        return new OutboxEvent(DestinationKey.generate(), integrationEvent, topic);
    }

}
