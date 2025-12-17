package pl.feature.toggle.service;

import org.testcontainers.kafka.KafkaContainer;

class KafkaBrokerContainer {

    private static final KafkaContainer INSTANCE = new KafkaContainer("apache/kafka:4.1.0");

    static {
        INSTANCE.start();
    }

    static KafkaContainer getInstance() {
        return INSTANCE;
    }

}
