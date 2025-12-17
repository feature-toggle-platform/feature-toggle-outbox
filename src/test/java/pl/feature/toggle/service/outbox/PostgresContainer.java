package pl.feature.toggle.service.outbox;

import org.testcontainers.containers.PostgreSQLContainer;

class PostgresContainer {

    private static final PostgreSQLContainer<?> INSTANCE =
            new PostgreSQLContainer<>("postgres:16");

    static {
        INSTANCE.start();
    }

    static PostgreSQLContainer<?> getInstance() {
        return INSTANCE;
    }

}
