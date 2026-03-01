package com.company.order.application.service;

import com.company.order.application.factory.OrderStrategyFactory;
import com.company.order.domain.model.*;
import com.company.order.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service d'orchestration de commandes.
 *
 * C'est ici que se fait le vrai travail de traitement d'une commande.
 * Ce service est appelé par le KafkaOrderConsumer après réception
 * d'un événement ORDER_CREATED.
 *
 * Pipeline réactif :
 * 1. Sélectionner la stratégie selon le type de commande (Strategy + Factory)
 * 2. Exécuter la stratégie (traitement polymorphique)
 * 3. Publier ORDER_COMPLETED ou ORDER_FAILED selon le résultat
 *
 * IMPORTANT : Aucun block() ici. Tout est non-bloquant.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderOrchestrationService {

    private final OrderStrategyFactory strategyFactory;
    private final EventPublisherPort eventPublisher;

    /**
     * Orchestre le traitement complet d'une commande.
     *
     * @param order Commande à traiter
     * @return Mono<Void> — se termine quand tout le pipeline est exécuté
     */
    public Mono<Void> orchestrate(Order order) {
        log.info("[correlationId={}] Starting orchestration for orderId={} type={}",
                order.correlationId(), order.id(), order.type());

        return Mono.fromSupplier(() -> strategyFactory.getStrategy(order.type()))
                .flatMap(strategy -> strategy.process(order))
                .then(publishCompleted(order))
                .doOnSuccess(v -> log.info("[correlationId={}] Orchestration COMPLETED for orderId={}",
                        order.correlationId(), order.id()))
                .onErrorResume(e -> handleError(order, e));
    }

    /**
     * Publie l'événement de succès.
     */
    private Mono<Void> publishCompleted(Order order) {
        OrderEvent completedEvent = OrderEvent.completed(order);
        return eventPublisher.publish(completedEvent);
    }

    /**
     * Gère les erreurs en publiant un événement ORDER_FAILED.
     *
     * Note : On loggue l'erreur et on publie ORDER_FAILED,
     * mais on re-propage l'exception pour que Kafka puisse retry.
     */
    private Mono<Void> handleError(Order order, Throwable error) {
        log.error("[correlationId={}] Orchestration FAILED for orderId={}: {}",
                order.correlationId(), order.id(), error.getMessage());

        OrderEvent failedEvent = OrderEvent.failed(order);

        return eventPublisher.publish(failedEvent)
                .then(Mono.error(error)); // Re-propage pour déclencher le retry Kafka
    }
}
