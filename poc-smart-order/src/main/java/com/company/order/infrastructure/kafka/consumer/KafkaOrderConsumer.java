package com.company.order.infrastructure.kafka.consumer;

import com.company.order.application.service.OrderOrchestrationService;
import com.company.order.domain.model.OrderEvent;
import com.company.order.infrastructure.kafka.config.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka pour les commandes créées.
 *
 * Responsabilité :
 * 1. Écouter le topic order.created
 * 2. Déclencher l'orchestration via OrderOrchestrationService
 *
 * Mécanisme de Retry avec Backoff Exponentiel :
 * @RetryableTopic crée automatiquement des topics intermédiaires :
 * - order.created-retry-1000  (après 1s)
 * - order.created-retry-2000  (après 2s)
 * - order.created-retry-4000  (après 4s)
 * - order.created-retry-8000  (après 8s)
 * Après 4 tentatives, l'événement est envoyé en DLT.
 *
 * Pourquoi Kafka retry plutôt qu'un retry applicatif ?
 * → Persistance : si l'app redémarre, le message est conservé sur Kafka
 * → Observabilité : les topics retry sont visibles dans les outils Kafka
 * → Isolation : les retries n'impactent pas le traitement normal
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderConsumer {

    private final OrderOrchestrationService orchestrationService;

    /**
     * Listener principal avec retry automatique.
     *
     * attempts = "4" → 1 tentative initiale + 3 retries = 4 au total
     * delay = 1000   → délai initial de 1 seconde
     * multiplier = 2 → backoff exponentiel (1s, 2s, 4s, 8s)
     */
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            ),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_DELAY_VALUE,
            dltTopicSuffix = ".DLT"
    )
    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "order-orchestrator-group"
    )
    public void onOrderCreated(
            OrderEvent event,
            @Header(name = "kafka_receivedPartitionId", required = false) Integer partition,
            @Header(name = "kafka_offset", required = false) Long offset
    ) {
        log.info("[correlationId={}] Received ORDER_CREATED event orderId={} partition={} offset={}",
                event.correlationId(), event.orderId(), partition, offset);

        // block() est nécessaire ici car le listener Kafka est impératif.
        // En production, on pourrait utiliser un ReactivePollableMessageSource
        // mais @RetryableTopic ne le supporte pas encore nativement.
        orchestrationService.orchestrate(event.payload()).block();

        log.info("[correlationId={}] ORDER_CREATED processing completed for orderId={}",
                event.correlationId(), event.orderId());
    }
}
