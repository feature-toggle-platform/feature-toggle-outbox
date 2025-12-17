package pl.feature.toggle.service;

import pl.feature.toggle.service.api.OutboxRepository;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static pl.feature.ftaas.outbox.jooq.tables.OutboxEvents.OUTBOX_EVENTS;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test-kafka")
@SpringBootTest
abstract class AbstractKafkaOutboxTest {

    @DynamicPropertySource
    static void pgProps(DynamicPropertyRegistry r) {
        var pg = PostgresContainer.getInstance();
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @DynamicPropertySource
    static void kafkaProps(DynamicPropertyRegistry r) {
        var kafkaContainer = KafkaBrokerContainer.getInstance();
        r.add("outbox.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        r.add("outbox.kafka.bootstrapServers", kafkaContainer::getBootstrapServers);
        r.add("outbox.kafka.props[delivery.timeout.ms]", () -> "5000");
        r.add("outbox.kafka.props[request.timeout.ms]", () -> "3000");
        r.add("outbox.kafka.props[max.block.ms]", () -> "3000");
        r.add("outbox.kafka.client-id", () -> "outbox-it");
        r.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private DSLContext dslContext;

    @Autowired
    protected OutboxRepository repository;

    @Autowired
    protected OutboxProperties properties;


    @AfterEach
    void tearDown() {
        clearOutboxTable();
    }

    private void clearOutboxTable() {
        dslContext.deleteFrom(OUTBOX_EVENTS).execute();
    }

    protected List<Outbox> findAllOutboxes() {
        return dslContext.selectFrom(OUTBOX_EVENTS)
                .fetch()
                .map(OutboxMapper::toDomain);
    }

}
