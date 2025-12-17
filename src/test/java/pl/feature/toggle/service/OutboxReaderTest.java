package pl.feature.toggle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static pl.feature.toggle.service.Status.Outbox.NEW;
import static org.assertj.core.api.Assertions.assertThat;

class OutboxReaderTest extends AbstractOutboxTest {

    private OutboxSchedulerReader reader;
    private InMemoryOutboxPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new InMemoryOutboxPublisher();
        var fakeAuditOutbox = new FakeAuditOutbox();
        this.reader = new OutboxSchedulerReader(repository, properties, publisher, fakeAuditOutbox);
    }


    @Test
    @DisplayName("Should do nothing if there is no unprocessed outboxes")
    void test01() {
        // given
        var currentOutboxes = findAllOutboxes(NEW);
        assertThat(currentOutboxes).isEmpty();

        // when
        reader.process();

        // then
        var outboxesAfterProcess = findAllOutboxes(NEW);
        assertThat(outboxesAfterProcess).isEmpty();
    }

    @Test
    @DisplayName("Should publish successfully unprocessed outbox")
    void test02() {
        // given
        var outbox = FakeOutboxBuilder.builder()
                .build();
        createOutbox(outbox);
        var allOutboxes = findAllOutboxes(NEW);
        assertThat(allOutboxes).hasSize(1);

        // when
        reader.process();

        // then
        final var outboxesAfterProcess = findAllOutboxes(NEW);
        assertThat(outboxesAfterProcess).isEmpty();
    }

    @Test
    @DisplayName("Should increase attempts when something went wrong while publishing message")
    void test03() {
        // given
        var errorMsg = "Test";
        publisher.exceptionOnNextPublish(errorMsg);
        var outbox = FakeOutboxBuilder.builder()
                .build();
        createOutbox(outbox);

        // when
        reader.process();

        // then
        var outboxesAfterProcess = findAllOutboxes();
        assertThat(outboxesAfterProcess).hasSize(1);
        var targedOutbox = outboxesAfterProcess.getFirst();
        assertThat(targedOutbox.attempts().attempt()).isEqualTo(1);
        assertThat(targedOutbox.attempts().lastErrorMessage()).isNotNull();
        assertThat(targedOutbox.attempts().limitReached()).isFalse();
        assertThat(targedOutbox.status().isNew()).isTrue();
    }

    @Test
    @DisplayName("Should increase attempts and mark outbox as failed if attempts limit has been reached")
    void test04() {
        // given
        publisher.exceptionOnNextPublish("test");
        var outbox = FakeOutboxBuilder.builder()
                .withAttempts(Attempts.zero(1))
                .build();
        createOutbox(outbox);

        // when
        reader.process();

        // then
        var outboxesAfterProcess = findAllOutboxes();
        assertThat(outboxesAfterProcess).hasSize(1);
        var targedOutbox = outboxesAfterProcess.getFirst();
        assertThat(targedOutbox.attempts().attempt()).isEqualTo(1);
        assertThat(targedOutbox.attempts().lastErrorMessage()).isNotEmpty();
        assertThat(targedOutbox.attempts().limitReached()).isTrue();
        assertThat(targedOutbox.status().isFailed()).isTrue();
    }

    @Test
    @DisplayName("Should check if last error msg is replaced when something went wrong twice")
    void test05(){
        // given
        var expectedErrorMessage = "Exception";
        publisher.exceptionOnNextPublish("test");
        var outbox = FakeOutboxBuilder.builder()
                .build();
        createOutbox(outbox);
        reader.process();
        var outboxesAfterProcess = findAllOutboxes();
        assertThat(outboxesAfterProcess).hasSize(1);
        var targedOutbox = outboxesAfterProcess.getFirst();
        assertThat(targedOutbox.attempts().attempt()).isEqualTo(1);
        assertThat(targedOutbox.attempts().lastErrorMessage()).isNotNull();
        assertThat(targedOutbox.attempts().limitReached()).isFalse();
        assertThat(targedOutbox.status().isNew()).isTrue();

        // when
        publisher.exceptionOnNextPublish(expectedErrorMessage);
        reader.process();

        // then
        outboxesAfterProcess = findAllOutboxes();
        targedOutbox = outboxesAfterProcess.getFirst();
        assertThat(targedOutbox.attempts().lastErrorMessage()).isNotEqualTo(expectedErrorMessage);
    }

    @Test
    @DisplayName("Should check if fetching unprocessed outboxes mark them as processing")
    void test06(){
        // given
        var outbox = FakeOutboxBuilder.builder()
                .build();
        createOutbox(outbox);

        // when
        var unprocessedOutboxes = repository.findUnprocessedOutboxes(1);

        // then
        assertThat(unprocessedOutboxes).hasSize(1);
        var targetOutbox = unprocessedOutboxes.getFirst();
        assertThat(targetOutbox.status().isProcessing()).isTrue();
    }

}
