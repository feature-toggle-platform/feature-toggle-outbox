package pl.feature.toggle.service;

import com.ftaas.contracts.event.projects.ProjectCreated;
import pl.feature.toggle.service.api.OutboxWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxWriterTest extends AbstractOutboxTest {

    private OutboxWriter outboxWriter;

    @BeforeEach
    void setUp() {
        this.outboxWriter = new OutboxWriterService(repository, properties, new ApplicationInfoProvider());
    }

    @Test
    @DisplayName("Should successfully save outbox")
    void test01() {
        // given
        var event = ProjectCreated.projectCreatedEventBuilder()
                .projectId(UUID.randomUUID())
                .projectName("TEST")
                .build();

        // when
        outboxWriter.write(event, "test");

        // then
        var unprocessedOutboxes = repository.findUnprocessedOutboxes(10);
        assertThat(unprocessedOutboxes).hasSize(1);
    }


}
