package com.company.order.infrastructure.kafka.consumer;

import com.company.order.domain.model.OrderEvent;
import com.company.order.infrastructure.kafka.config.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Consumer de la Dead Letter Queue (DLQ).
 *
 * Reçoit les messages qui ont épuisé tous leurs retries.
 * Ce consumer NE doit PAS re-lancer le traitement automatiquement
 * (risque de boucle infinie).
 *
 * Actions en production :
 * 1. Log structuré avec tous les détails pour le diagnostic
 * 2. Persistance en base (table order_failures) pour audit
 * 3. Alerte monitoring (PagerDuty, Datadog, etc.)
 * 4. Optionnel : notification équipe support
 *
 * Traitement manuel requis après examen du problème.
 */
@Slf4j
@Component
public class KafkaDeadLetterConsumer {

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED_DLT,
            groupId = "order-dlq-group"
    )
    public void onDeadLetter(
            OrderEvent event,
            @Header(name = "kafka_dlt-exception-message", required = false) String exceptionMessage,
            @Header(name = "kafka_dlt-original-topic", required = false) String originalTopic
    ) {
        // Log structuré pour permettre une analyse rapide
        log.error("""
                ====================================================
                DEAD LETTER QUEUE — Message non traitable
                ====================================================
                orderId      : {}
                correlationId: {}
                orderType    : {}
                customerId   : {}
                amount       : {} centimes
                originalTopic: {}
                error        : {}
                ====================================================
                """,
                event.orderId(),
                event.correlationId(),
                event.orderType(),
                event.payload() != null ? event.payload().customerId() : "N/A",
                event.payload() != null ? event.payload().amountInCents() : "N/A",
                originalTopic,
                exceptionMessage
        );

        // TODO (production) :
        // 1. dlqRepository.save(new DeadLetterEntry(event, exceptionMessage));
        // 2. alertingService.sendAlert("DLQ message received for order: " + event.orderId());
        // 3. meterRegistry.counter("orders.dlq").increment();
    }
}
