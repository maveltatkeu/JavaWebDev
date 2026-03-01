package com.company.order.infrastructure.kafka.consumer;

import com.company.order.application.factory.OrderStrategyFactory;
import com.company.order.domain.model.*;
import com.company.order.domain.port.out.EventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * CONSUMER KAFKA – Point d'entrée du traitement asynchrone des commandes.
 *
 * Consomme le topic "order.created" et orchestre le traitement
 * via la OrderStrategyFactory (sélection polymorphique de la stratégie).
 *
 * RETRY EXPONENTIEL configuré avec @RetryableTopic :
 * - 4 tentatives maximum
 * - Délai initial : 1 seconde
 * - Multiplicateur : x2 → 1s, 2s, 4s, 8s
 *
 * Kafka crée automatiquement des topics intermédiaires :
 * order.created-retry-1000, order.created-retry-2000, etc.
 *
 * Après épuisement : envoi vers la DLT (Dead Letter Topic) order.created.DLT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final OrderStrategyFactory strategyFactory;
    private final EventPublisherPort eventPublisherPort;
    private final ObjectMapper objectMapper;

    @RetryableTopic(
            attempts = "4",                    // 1 essai initial + 3 retries
            backoff = @Backoff(
                    delay = 1000,              // 1 seconde initial
                    multiplier = 2             // Double à chaque retry
            ),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_DELAY_VALUE,
            autoCreateTopics = "true"
    )
    @KafkaListener(
            topics = "${kafka.topics.order-created:order.created}",
            groupId = "${kafka.consumer.group-id:order-orchestrator-group}"
    )
    public void consume(String message,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Received message from topic={}, offset={}", topic, offset);

        try {
            // Désérialisation du message Kafka en OrderEvent
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            log.info("Processing event {} for order {}", event.eventType(), event.orderId());

            // Construction d'un Order minimal depuis l'événement
            // En production : charger depuis une base de données
            Order order = reconstructOrder(event);

            // Sélection dynamique de la stratégie + traitement
            // block() est utilisé ici car le consumer Kafka n'est pas dans un contexte réactif
            // Alternative production : utiliser un Scheduler dédié
            strategyFactory.getStrategy(order.type())
                    .process(order)
                    .block();

        } catch (Exception e) {
            log.error("Error processing message at offset {}: {}", offset, e.getMessage());
            // Relance l'exception pour déclencher le mécanisme de retry Kafka
            throw new RuntimeException("Order processing failed", e);
        }
    }

    /**
     * Handler de la Dead Letter Topic.
     * Appelé après épuisement de tous les retries.
     *
     * Actions : log structuré, persistance, alerte monitoring.
     */
    @DltHandler
    public void handleDlt(String message,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          @Header(KafkaHeaders.OFFSET) long offset) {

        log.error("=== DLT HANDLER === Message ended in DLT. topic={}, offset={}", topic, offset);
        log.error("Failed message: {}", message);

        // TODO production :
        // 1. Persister en base pour analyse humaine
        // 2. Déclencher une alerte PagerDuty/Opsgenie
        // 3. Publier une métrique Micrometer
        // 4. Notifier le client de l'échec de sa commande

        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            publishFailedEvent(event);
        } catch (Exception e) {
            log.error("Could not parse DLT message: {}", e.getMessage());
        }
    }

    private void publishFailedEvent(OrderEvent originalEvent) {
        OrderEvent failedEvent = new OrderEvent(
                UUID.randomUUID().toString(),
                originalEvent.orderId(),
                originalEvent.orderType(),
                "ORDER_FAILED",
                originalEvent.correlationId(),
                Instant.now(),
                "{\"reason\":\"exhausted_retries\"}"
        );
        eventPublisherPort.publish(failedEvent).subscribe();
    }

    /**
     * Reconstruction d'un Order depuis l'événement Kafka.
     * En production : charger depuis la base de données via un repository.
     */
    private Order reconstructOrder(OrderEvent event) {
        return switch (event.orderType()) {
            case PHYSICAL -> new PhysicalOrder(
                    event.orderId(), "unknown", 0L,
                    OrderStatus.PROCESSING, event.correlationId(),
                    "SKU-001", 1, "N/A"
            );
            case DIGITAL -> new DigitalOrder(
                    event.orderId(), "unknown", 0L,
                    OrderStatus.PROCESSING, event.correlationId(),
                    "ANNUAL", "PROD-001"
            );
            case TOPUP -> new TopUpOrder(
                    event.orderId(), "unknown", 0L,
                    OrderStatus.PROCESSING, event.correlationId(),
                    "WALLET-001"
            );
        };
    }
}
