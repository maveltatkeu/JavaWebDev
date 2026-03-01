package com.company.order.infrastructure.kafka.producer;

import com.company.order.domain.model.OrderEvent;
import com.company.order.domain.port.out.EventPublisherPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * ADAPTATEUR SORTANT – Implémentation Kafka du port EventPublisherPort.
 *
 * Traduit les OrderEvent du domaine en messages Kafka.
 * Le domaine ne sait pas que c'est Kafka ; il n'appelle que l'interface EventPublisherPort.
 *
 * Utilisation de KafkaTemplate avec Mono.fromFuture() pour rester non-bloquant.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topics.order-created:order.created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-completed:order.completed}")
    private String orderCompletedTopic;

    @Value("${kafka.topics.order-failed:order.failed}")
    private String orderFailedTopic;

    @Override
    public Mono<Void> publish(OrderEvent event) {
        return Mono.fromCallable(() -> serializeEvent(event))
                // fromCompletionStage wrap le Future Kafka dans un Mono réactif
                .flatMap(payload -> Mono.fromCompletionStage(
                        kafkaTemplate.send(resolveTopic(event), event.orderId(), payload)
                                .completable()
                ))
                .doOnSuccess(result -> log.info(
                        "Event {} published to topic {} for order {}",
                        event.eventType(), resolveTopic(event), event.orderId()
                ))
                .doOnError(e -> log.error(
                        "Failed to publish event {} for order {}: {}",
                        event.eventType(), event.orderId(), e.getMessage()
                ))
                .then();
    }

    /**
     * Détermine le topic de destination selon le type d'événement.
     */
    private String resolveTopic(OrderEvent event) {
        return switch (event.eventType()) {
            case "ORDER_CREATED" -> orderCreatedTopic;
            case "ORDER_COMPLETED" -> orderCompletedTopic;
            default -> orderFailedTopic;
        };
    }

    private String serializeEvent(OrderEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event: " + event.eventId(), e);
        }
    }
}
