package pl.feature.toggle.service.outbox;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties("outbox")
class OutboxProperties {

    private int batchSize = 100;
    private Duration poolIntervalMs = Duration.ofSeconds(10);
    private Duration initialDelayMs = Duration.ZERO;
    private int attemptLimit = 3;
    private Kafka kafka = new Kafka();

    @Getter
    @Setter
    static class Kafka {
        private boolean enabled = true;
        private String bootstrapServers;
        private String clientId = "ftaas-outbox";
        private Duration deliveryTimeoutMs = Duration.ofSeconds(10);
        private Duration requestTimeoutMs = Duration.ofSeconds(5);
        private Duration maxBlockMs = Duration.ofSeconds(5);
        private List<Topic> topics = new ArrayList<>();
        private Map<String, String> props = new HashMap<>();
    }

    @Getter
    @Setter
    static class Topic{
        private String name;
        private int partitions;
        private short replicationFactor;
    }

}
