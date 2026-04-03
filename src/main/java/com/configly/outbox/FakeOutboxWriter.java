package com.configly.outbox;

import com.configly.contracts.shared.IntegrationEvent;
import com.configly.outbox.api.OutboxEvent;
import com.configly.outbox.api.OutboxWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeOutboxWriter implements OutboxWriter {

    private final Map<String, List<IntegrationEvent>> events = new HashMap<>();

    @Override
    public void write(OutboxEvent outboxEvent) {
        events.computeIfAbsent(outboxEvent.topic().topicName(), k -> new ArrayList<>()).add(outboxEvent.event());
    }

    public List<IntegrationEvent> eventsForTopic(String topic) {
        return events.get(topic);
    }

    public int eventCountForTopic(String topic) {
        return eventsForTopic(topic).size();
    }

    public void clear() {
        events.clear();
    }

    public boolean eventExistsForTopic(String topic, IntegrationEvent event) {
        if (!events.containsKey(topic)) return false;
        return eventsForTopic(topic).contains(event);
    }

    public String getTopicForEvent(IntegrationEvent event) {
        return events.entrySet().stream()
                .filter(entry -> entry.getValue().contains(event))
                .findFirst()
                .orElseThrow()
                .getKey();
    }

    public boolean containsEvent(IntegrationEvent event) {
        return events.values().stream().anyMatch(list -> list.contains(event));
    }


    public boolean containsEventOfType(String topic, Class<?> eventClass) {
        if (!events.containsKey(topic)) return false;
        return events.get(topic).stream().anyMatch(event -> event.getClass().equals(eventClass));
    }

    public boolean containsEventOfType(Class<?> eventClass) {
        return events.values().stream().anyMatch(list -> list.stream().anyMatch(event -> event.getClass().equals(eventClass)));
    }

    public boolean hasEventTypeCountForTopic(String topic, Class<?> eventClass, int eventCount) {
        return events.containsKey(topic)
                && events.get(topic).stream()
                .filter(event -> event.getClass().equals(eventClass))
                .count() >= eventCount;
    }

    public <T extends IntegrationEvent> T lastEventOfType(String topic, Class<T> eventType) {
        if (!events.containsKey(topic)) {
            throw new AssertionError("No events published for topic: " + topic);
        }

        return events.get(topic).stream()
                .filter(eventType::isInstance)
                .map(eventType::cast)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new AssertionError(
                        "No event of type " + eventType.getSimpleName() + " published for topic: " + topic
                ));
    }

    public boolean noEventsHaveBeenPublished() {
        return events.isEmpty();
    }
}
