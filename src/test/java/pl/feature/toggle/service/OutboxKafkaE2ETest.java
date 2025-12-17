package pl.feature.toggle.service;

import com.ftaas.contracts.event.projects.ProjectCreated;
import pl.feature.toggle.service.api.OutboxReader;
import pl.feature.toggle.service.api.OutboxWriter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class OutboxKafkaE2ETest extends AbstractKafkaOutboxTest {

    @Autowired
    private OutboxWriter outboxWriter;
    @Autowired
    private OutboxReader reader;

    private KafkaConsumer<String, byte[]> consumer;

    @BeforeEach
    void setUpConsumer() {
        var props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaBrokerContainer.getInstance().getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "outbox-e2e-" + UUID.randomUUID());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());

        consumer = new KafkaConsumer<>(props);
    }

    @AfterEach
    void tearDownConsumer() {
        if (consumer != null) consumer.close();
    }

    @Test
    @DisplayName("Should publish event to kafka topic")
    void test01() {
        // given
        var topic = "test";
        consumer.subscribe(List.of(topic));

        var event = ProjectCreated.projectCreatedEventBuilder()
                .projectId(UUID.randomUUID())
                .projectName("TEST")
                .build();

        // when
        outboxWriter.write(event, topic);
        reader.process();

        // then
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500));
            assertThat(records.count()).isEqualTo(1);

            var rec = records.iterator().next();
            assertThat(rec.topic()).isEqualTo(topic);
            assertThat(rec.key()).isNotBlank();
            assertThat(rec.value()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("Should mark outbox as PROCESSED after successful publish")
    void test02() {
        // given
        var topic = "test";
        var event = ProjectCreated.projectCreatedEventBuilder()
                .projectId(UUID.randomUUID())
                .projectName("TEST")
                .build();

        // when
        outboxWriter.write(event, topic);
        reader.process();

        // then
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var all = findAllOutboxes();
            assertThat(all).hasSize(1);

            var outbox = all.getFirst();

            assertThat(outbox.status()).isEqualTo(Status.createPublished());
            assertThat(outbox.status().isNew()).isFalse();
            assertThat(outbox.status().isFailed()).isFalse();
        });
    }

}
