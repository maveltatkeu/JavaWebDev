package com.company.order.application.strategy;

import com.company.order.domain.model.*;
import com.company.order.domain.port.out.EventPublisherPort;
import com.company.order.domain.port.out.InventoryPort;
import com.company.order.domain.port.out.PaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Stratégie de traitement pour les commandes PHYSIQUES.
 *
 * Pipeline réactif (sans block()) :
 * 1. Paiement (PaymentPort)
 * 2. Réservation inventaire (InventoryPort)
 * 3. Publication événement order.completed
 *
 * En cas d'échec à n'importe quelle étape, l'erreur remonte
 * et Kafka déclenche le mécanisme de retry.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PhysicalOrderStrategy implements OrderProcessingStrategy {

    private final PaymentPort paymentPort;
    private final InventoryPort inventoryPort;
    private final EventPublisherPort eventPublisherPort;

    @Override
    public Mono<Void> process(Order order) {
        log.info("[PHYSICAL] Processing order {} for customer {}",
                order.id(), order.customerId());

        return paymentPort.charge(order)
                // flatMap chaîne les opérations réactives : chaque étape
                // démarre quand la précédente est terminée avec succès
                .flatMap(paymentResult -> {
                    log.info("[PHYSICAL] Payment OK (txId={}), reserving inventory",
                            paymentResult.transactionId());
                    return inventoryPort.reserve(order);
                })
                // then() exécute une action après succès sans propager de valeur
                .then(publishCompletedEvent(order))
                // doOnError log l'erreur sans l'intercepter (elle remonte vers Kafka)
                .doOnError(error -> log.error("[PHYSICAL] Order {} failed: {}",
                        order.id(), error.getMessage()));
    }

    @Override
    public OrderType supportedType() {
        return OrderType.PHYSICAL;
    }

    private Mono<Void> publishCompletedEvent(Order order) {
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                order.id(),
                OrderType.PHYSICAL,
                "ORDER_COMPLETED",
                order.correlationId(),
                Instant.now(),
                "{\"status\":\"COMPLETED\"}"
        );
        return eventPublisherPort.publish(event);
    }
}
