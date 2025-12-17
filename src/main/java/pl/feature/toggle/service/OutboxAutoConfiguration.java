package pl.feature.toggle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.integration.spring.SpringLiquibase;
import org.jooq.DSLContext;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jooq.autoconfigure.JooqAutoConfiguration;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.feature.toggle.service.api.*;

import javax.sql.DataSource;


@AutoConfiguration(after = {DataSourceAutoConfiguration.class, LiquibaseAutoConfiguration.class, JooqAutoConfiguration.class, OutboxKafkaAutoConfiguration.class})
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
class OutboxAutoConfiguration {

    @Bean
    @ConditionalOnClass(SpringLiquibase.class)
    @ConditionalOnBean(DataSource.class)
    @ConditionalOnProperty(prefix = "outbox.liquibase", name = "enabled", havingValue = "true", matchIfMissing = true)
    SpringLiquibase outboxLiquibase(DataSource dataSource) {
        SpringLiquibase lb = new SpringLiquibase();
        lb.setDataSource(dataSource);
        lb.setChangeLog("classpath:/META-INF/ftaas-outbox/changelog/outbox.changelog-master.xml");
        lb.setShouldRun(true);
        lb.setLiquibaseTablespace(null);
        return lb;
    }

    @Bean
    @ConditionalOnMissingBean(Settings.class)
    Settings settings() {
        return new Settings()
                .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }

    @Bean
    @ConditionalOnMissingBean
    ApplicationInfoProvider applicationInfoProvider() {
        return new ApplicationInfoProvider();
    }

    @Bean
    @ConditionalOnClass(ObjectMapper.class)
    @ConditionalOnMissingBean(ObjectMapper.class)
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean(OutboxAudit.class)
    OutboxAudit outboxAudit() {
        return new Slf4jOutboxAudit();
    }

    @Bean
    @ConditionalOnBean(DSLContext.class)
    @ConditionalOnMissingBean(OutboxRepository.class)
    OutboxRepository outboxRepositoryJooq(DSLContext dsl) {
        return new OutboxJooqRepository(dsl);
    }

    @Bean
    @ConditionalOnBean(OutboxRepository.class)
    @ConditionalOnMissingBean(OutboxWriter.class)
    OutboxWriter outboxWriter(final OutboxRepository outboxRepository, final OutboxProperties props, final ApplicationInfoProvider applicationInfoProvider) {
        return new OutboxWriterService(outboxRepository, props, applicationInfoProvider);
    }

    @Bean
    @ConditionalOnBean({OutboxRepository.class, OutboxPublisher.class})
    @ConditionalOnMissingBean(OutboxReader.class)
    OutboxReader outboxReader(OutboxRepository outboxRepository, OutboxProperties props, OutboxPublisher publisher, OutboxAudit audit) {
        return new OutboxSchedulerReader(outboxRepository, props, publisher, audit);
    }
}
