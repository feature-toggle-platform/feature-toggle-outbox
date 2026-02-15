package pl.feature.toggle.service.outbox;

import pl.feature.toggle.service.outbox.api.OutboxRepository;
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
@ActiveProfiles("test")
@SpringBootTest
abstract class AbstractOutboxTest {

    @DynamicPropertySource
    static void pgProps(DynamicPropertyRegistry r) {
        var pg = PostgresContainer.getInstance();
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    @Autowired
    private DSLContext dslContext;

    @Autowired
    protected OutboxRepository repository;

    @Autowired
    protected OutboxProperties properties;

    @Autowired
    protected OutboxMapper outboxMapper;


    @AfterEach
    void tearDown() {
        clearOutboxTable();
    }

    protected List<Outbox> findAllOutboxes(Status.Outbox status) {
        return dslContext.selectFrom(OUTBOX_EVENTS)
                .where(OUTBOX_EVENTS.STATUS.eq(status.name()))
                .fetch()
                .map(outboxMapper::toDomain);
    }


    protected List<Outbox> findAllOutboxes() {
        return dslContext.selectFrom(OUTBOX_EVENTS)
                .fetch()
                .map(outboxMapper::toDomain);
    }

    protected void createOutbox(Outbox outbox) {
        repository.save(outbox);
    }

    private void clearOutboxTable() {
        dslContext.deleteFrom(OUTBOX_EVENTS).execute();
    }

}
