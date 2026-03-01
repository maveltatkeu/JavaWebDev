package com.company.order.application.strategy;

import com.company.order.domain.model.*;
import com.company.order.domain.port.out.EventPublisherPort;
import com.company.order.domain.port.out.PaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * Stratégie de traitement pour les RECHARGES (TopUp).
 *
 * Pipeline simplifié :
 * 1. Paiement uniquement
 * 2. Publication événement order.completed
 *
 * Ni inventaire ni facturation : la recharge est instantanée.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TopUpOrderStrategy implements OrderProcessingStrategy {

    private final PaymentPort paymentPort;
    private final EventPublisherPort eventPublisherPort;

    @Override
    public Mono<Void> process(Order order) {
        log.info("[TOPUP] Processing recharge for order {}", order.id());

        return paymentPort.charge(order)
                .then(publishCompletedEvent(order))
                .doOnError(error -> log.error("[TOPUP] Order {} failed: {}",
                        order.id(), error.getMessage()));
    }

    @Override
    public OrderType supportedType() {
        return OrderType.TOPUP;
    }

    private Mono<Void> publishCompletedEvent(Order order) {
        OrderEvent event = new OrderEvent(
                UUID.randomUUID().toString(),
                order.id(),
                OrderType.TOPUP,
                "ORDER_COMPLETED",
                order.correlationId(),
                Instant.now(),
                "{\"status\":\"COMPLETED\"}"
        );
        return eventPublisherPort.publish(event);
    }
}
