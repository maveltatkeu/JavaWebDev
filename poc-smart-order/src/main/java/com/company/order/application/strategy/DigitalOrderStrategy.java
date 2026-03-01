package com.company.order.application.strategy;

import com.company.order.domain.model.*;
import com.company.order.domain.port.out.BillingPort;
import com.company.order.domain.port.out.EventPublisherPort;
import com.company.order.domain.port.out.PaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Stratégie de traitement pour les commandes DIGITALES.
 *
 * Pipeline réactif :
 * 1. Paiement
 * 2. Génération de la facture (BillingPort)
 * 3. Publication événement order.completed
 *
 * Pas de réservation d'inventaire : les produits digitaux sont immatériels.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DigitalOrderStrategy implements OrderProcessingStrategy {

    private final PaymentPort paymentPort;
    private final BillingPort billingPort;
    private final EventPublisherPort eventPublisherPort;

    @Override
    public Mono<Void> process(Order order) {
        log.info("[DIGITAL] Processing order {}", order.id());

        return paymentPort.charge(order)
                .flatMap(paymentResult -> {
                    log.info("[DIGITAL] Payment OK, generating invoice");
                    return billingPort.generateInvoice(order);
                })
                .flatMap(invoiceId -> {
                    log.info("[DIGITAL] Invoice {} generated, publishing event", invoiceId);
                    return publishCompletedEvent(order, invoiceId);
                })
                .doOnError(error -> log.error("[DIGITAL] Order {} failed: {}",
                        order.id(), error.getMessage()));
    }

    @Override
    public OrderType supportedType() {
        return OrderType.DIGITAL;
    }

    private Mono<Void> publishCompletedEvent(Order order, String invoiceId) {
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                order.id(),
                OrderType.DIGITAL,
                "ORDER_COMPLETED",
                order.correlationId(),
                Instant.now(),
                "{\"status\":\"COMPLETED\",\"invoiceId\":\"" + invoiceId + "\"}"
        );
        return eventPublisherPort.publish(event);
    }
}
