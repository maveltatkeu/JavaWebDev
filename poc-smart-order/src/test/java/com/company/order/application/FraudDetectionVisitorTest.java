package com.company.order.application;

import com.company.order.application.visitor.FraudDetectionVisitor;
import com.company.order.domain.model.PhysicalOrder;
import com.company.order.domain.model.TopUpOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * Tests unitaires de FraudDetectionVisitor.
 *
 * Vérifie les seuils de détection de fraude par type de commande.
 */
@DisplayName("FraudDetectionVisitor — Tests unitaires")
class FraudDetectionVisitorTest {

    private final FraudDetectionVisitor visitor = new FraudDetectionVisitor();

    @Test
    @DisplayName("PhysicalOrder sous le seuil → pas de fraude détectée")
    void physicalOrder_belowThreshold_shouldPass() {
        var order = new PhysicalOrder("o-1", "c-1", 10_000L, "corr-1", "addr", "sku", 1);

        StepVerifier.create(visitor.visit(order))
                .verifyComplete();
    }

    @Test
    @DisplayName("PhysicalOrder au-dessus du seuil (500€) → FraudDetectedException")
    void physicalOrder_aboveThreshold_shouldThrowFraudException() {
        var order = new PhysicalOrder("o-2", "c-1", 60_000L, "corr-2", "addr", "sku", 1);

        StepVerifier.create(visitor.visit(order))
                .expectError(FraudDetectionVisitor.FraudDetectedException.class)
                .verify();
    }

    @Test
    @DisplayName("TopUpOrder au-dessus du seuil (200€) → FraudDetectedException")
    void topUpOrder_aboveThreshold_shouldThrowFraudException() {
        var order = new TopUpOrder("o-3", "c-1", 25_000L, "corr-3", "acc-001", "provider-x");

        StepVerifier.create(visitor.visit(order))
                .expectError(FraudDetectionVisitor.FraudDetectedException.class)
                .verify();
    }
}
