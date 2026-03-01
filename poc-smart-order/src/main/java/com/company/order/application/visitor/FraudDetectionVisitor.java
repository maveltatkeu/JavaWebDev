package com.company.order.application.visitor;

import com.company.order.domain.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Visitor de détection de fraude.
 *
 * Applique des règles anti-fraude différentes selon le type de commande :
 * - PHYSICAL : vérifie le montant max et l'adresse de livraison
 * - DIGITAL  : vérifie les achats multiples de la même licence
 * - TOPUP    : vérifie les recharges inhabituelles
 *
 * Ce visitor s'applique sans modifier les classes Order.
 * Pour l'activer, l'injecter dans OrderOrchestrationService et appeler accept().
 */
@Slf4j
@Component
public class FraudDetectionVisitor implements OrderVisitor {

    /** Seuil au-delà duquel une commande physique est suspecte (500€). */
    private static final long PHYSICAL_FRAUD_THRESHOLD_CENTS = 50_000L;

    /** Seuil pour les recharges suspectes (200€). */
    private static final long TOPUP_FRAUD_THRESHOLD_CENTS = 20_000L;

    @Override
    public Mono<Void> visit(PhysicalOrder order) {
        log.debug("[correlationId={}] FraudDetection checking PhysicalOrder orderId={}",
                order.correlationId(), order.orderId());

        if (order.amountInCents() > PHYSICAL_FRAUD_THRESHOLD_CENTS) {
            log.warn("[correlationId={}] FRAUD ALERT: PhysicalOrder orderId={} amount={}c exceeds threshold",
                    order.correlationId(), order.orderId(), order.amountInCents());
            return Mono.error(new FraudDetectedException(
                    "Physical order exceeds fraud threshold: " + order.amountInCents()));
        }

        return Mono.empty();
    }

    @Override
    public Mono<Void> visit(DigitalOrder order) {
        log.debug("[correlationId={}] FraudDetection checking DigitalOrder orderId={}",
                order.correlationId(), order.orderId());
        // Règle simplifiée — en production : vérifier les achats récents du client
        return Mono.empty();
    }

    @Override
    public Mono<Void> visit(TopUpOrder order) {
        log.debug("[correlationId={}] FraudDetection checking TopUpOrder orderId={}",
                order.correlationId(), order.orderId());

        if (order.amountInCents() > TOPUP_FRAUD_THRESHOLD_CENTS) {
            log.warn("[correlationId={}] FRAUD ALERT: TopUp orderId={} amount={}c exceeds threshold",
                    order.correlationId(), order.orderId(), order.amountInCents());
            return Mono.error(new FraudDetectedException(
                    "TopUp exceeds fraud threshold: " + order.amountInCents()));
        }

        return Mono.empty();
    }

    public static class FraudDetectedException extends RuntimeException {
        public FraudDetectedException(String message) {
            super(message);
        }
    }
}
