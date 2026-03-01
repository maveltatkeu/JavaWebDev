package com.company.order.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration des topics Kafka.
 *
 * Spring Kafka crée automatiquement les topics s'ils n'existent pas
 * (kafka.admin.auto-create=true par défaut en dev).
 *
 * En production : gérer les topics via Terraform/Helm ou un opérateur Kafka.
 */
@Configuration
public class KafkaTopicsConfig {

    @Value("${kafka.topics.order-created:order.created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-completed:order.completed}")
    private String orderCompletedTopic;

    @Value("${kafka.topics.order-failed:order.failed}")
    private String orderFailedTopic;

    /** Topic principal de création de commande – point d'entrée du flow */
    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(orderCreatedTopic)
                .partitions(3)   // 3 partitions = 3 consumers max en parallèle
                .replicas(1)     // 1 replica en dev, 3 minimum en production
                .build();
    }

    /** Topic de succès – consommé par les systèmes en aval (notification, reporting) */
    @Bean
    public NewTopic orderCompletedTopic() {
        return TopicBuilder.name(orderCompletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    /** Topic d'échec – consommé par le système d'alerting */
    @Bean
    public NewTopic orderFailedTopic() {
        return TopicBuilder.name(orderFailedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
