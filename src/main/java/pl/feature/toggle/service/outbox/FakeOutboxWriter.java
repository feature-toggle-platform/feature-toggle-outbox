package pl.feature.toggle.service.outbox;

import pl.feature.toggle.service.contracts.shared.IntegrationEvent;
import pl.feature.toggle.service.outbox.api.OutboxWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeOutboxWriter implements OutboxWriter {

    private final Map<String, List<IntegrationEvent>> events = new HashMap<>();

    @Override
    public void write(IntegrationEvent event, String topic) {
        events.computeIfAbsent(topic, k -> new ArrayList<>()).add(event);
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


    public boolean containsEventOfType(String topic, Class<?> projectCreatedClass) {
        if (!events.containsKey(topic)) return false;
        return events.get(topic).stream().anyMatch(event -> event.getClass().equals(projectCreatedClass));
    }

    public boolean containsEventOfType(Class<?> projectCreatedClass) {
        return events.values().stream().anyMatch(list -> list.stream().anyMatch(event -> event.getClass().equals(projectCreatedClass)));
    }
}
