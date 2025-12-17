package pl.feature.toggle.service;

import pl.feature.toggle.service.api.OutboxPublisher;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaAdmin.NewTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;

@AutoConfiguration
@ConditionalOnClass(org.springframework.kafka.core.KafkaTemplate.class)
@ConditionalOnProperty(prefix = "outbox.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
class OutboxKafkaAutoConfiguration {

    @Bean
    KafkaTemplate<String, Object> outboxKafkaTemplate(ProducerFactory<String, Object> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    ProducerFactory<String, Object> outboxProducerFactory(OutboxProperties props) {
        var cfg = new HashMap<String, Object>();
        cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getKafka().getBootstrapServers());
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        cfg.put(ProducerConfig.CLIENT_ID_CONFIG, props.getKafka().getClientId());
        cfg.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, props.getKafka().getDeliveryTimeoutMs());
        cfg.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, props.getKafka().getRequestTimeoutMs());
        cfg.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, props.getKafka().getMaxBlockMs());
        props.getKafka().getProps().forEach((kk, vv) -> {
            if (kk != null && vv != null) cfg.put(kk, vv);
        });

        return new DefaultKafkaProducerFactory<>(cfg);
    }

    @Bean
    @ConditionalOnMissingBean(KafkaAdmin.class)
    KafkaAdmin outboxKafkaAdmin(OutboxProperties props) {
        var cfg = new HashMap<String, Object>();
        cfg.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, props.getKafka().getBootstrapServers());
        var admin = new KafkaAdmin(cfg);
        admin.setAutoCreate(true);
        return admin;
    }

    @Bean
    OutboxPublisher kafkaOutboxPublisher(KafkaTemplate<String, Object> template) {
        return new OutboxKafkaPublisher(template);
    }

    @Bean
    @DependsOn("outboxKafkaAdmin")
    NewTopics outboxNewTopics(OutboxProperties props) {
        var topics = props.getKafka().getTopics().stream()
                .map(this::buildTopic)
                .toArray(NewTopic[]::new);
        return new NewTopics(topics);
    }

    private NewTopic buildTopic(OutboxProperties.Topic topic) {
        return TopicBuilder.name(topic.getName())
                .partitions(topic.getPartitions())
                .replicas(topic.getReplicationFactor())
                .build();
    }


}
