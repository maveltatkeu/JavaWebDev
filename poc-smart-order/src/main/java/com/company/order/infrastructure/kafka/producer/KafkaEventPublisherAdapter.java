package com.company.order.infrastructure.kafka.producer;

import com.company.order.domain.model.OrderEvent;
import com.company.order.domain.port.out.EventPublisherPort;
import com.company.order.infrastructure.kafka.config.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adaptateur Kafka sortant — implémentation de EventPublisherPort.
 *
 * Pattern Adapter : adapte l'API KafkaTemplate (imperative) au contrat
 * réactif (Mono) défini par le port de domaine.
 *
 * Route les événements vers le bon topic selon leur type :
 * - ORDER_CREATED    → order.created
 * - ORDER_COMPLETED  → order.completed
 * - ORDER_FAILED     → order.failed
 *
 * La clé Kafka est l'orderId, ce qui garantit que tous les événements
 * d'une même commande vont sur la même partition (ordering garanti).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Override
    public Mono<Void> publish(OrderEvent event) {
        String topic = resolveTopic(event);

        log.debug("[correlationId={}] Publishing event {} to topic {}",
                event.correlationId(), event.eventType(), topic);

        // Conversion de l'API imperative KafkaTemplate en Mono réactif
        return Mono.fromFuture(() ->
                kafkaTemplate.send(topic, event.orderId(), event)
                        .toCompletableFuture()
        )
        .doOnSuccess(result -> log.info("[correlationId={}] Event {} published to topic {} partition {} offset {}",
                event.correlationId(), event.eventType(), topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()))
        .doOnError(e -> log.error("[correlationId={}] Failed to publish event {} to topic {}",
                event.correlationId(), event.eventType(), topic, e))
        .then();
    }

    /**
     * Détermine le topic cible en fonction du type d'événement.
     */
    private String resolveTopic(OrderEvent event) {
        return switch (event.eventType()) {
            case ORDER_CREATED    -> KafkaTopics.ORDER_CREATED;
            case ORDER_PROCESSING -> KafkaTopics.ORDER_PROCESSING;
            case ORDER_COMPLETED  -> KafkaTopics.ORDER_COMPLETED;
            case ORDER_FAILED     -> KafkaTopics.ORDER_FAILED;
        };
    }
}
