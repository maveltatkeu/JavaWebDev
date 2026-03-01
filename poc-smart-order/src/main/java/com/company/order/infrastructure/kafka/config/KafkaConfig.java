package com.company.order.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration Kafka : création automatique des topics au démarrage.
 *
 * Spring Kafka crée les topics via l'AdminClient si ils n'existent pas.
 * En production, les topics seraient pré-créés avec des configurations
 * spécifiques (replication factor, retention, etc.).
 *
 * Partitions : 3 → permet le traitement parallèle par 3 consumers.
 * Replicas : 1 → pour le dev (en prod : 3 minimum).
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderProcessingTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_PROCESSING)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCompletedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_COMPLETED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderFailedTopic() {
        return TopicBuilder.name(KafkaTopics.ORDER_FAILED)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCreatedDltTopic() {
        // DLT (Dead Letter Topic) : pas de retry, rétention longue pour analyse
        return TopicBuilder.name(KafkaTopics.ORDER_CREATED_DLT)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
