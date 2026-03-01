package com.company.order.application.visitor;

import com.company.order.domain.model.DigitalOrder;
import com.company.order.domain.model.PhysicalOrder;
import com.company.order.domain.model.TopUpOrder;
import com.company.order.domain.exception.OrderProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Visiteur de VALIDATION – Vérifie les règles métier spécifiques à chaque type.
 *
 * Règles appliquées :
 * - PhysicalOrder : adresse de livraison obligatoire, quantité > 0
 * - DigitalOrder  : code produit obligatoire
 * - TopUpOrder    : walletId et montant minimum requis
 */
@Slf4j
@Component
public class OrderValidationVisitor implements OrderVisitor {

    /** Montant minimum pour une recharge (en centimes) */
    private static final long TOPUP_MIN_AMOUNT = 100L; // 1€

    @Override
    public void visit(PhysicalOrder order) {
        log.debug("Validating physical order {}", order.id());

        if (order.shippingAddress() == null || order.shippingAddress().isBlank()) {
            throw new OrderProcessingException(
                    "Shipping address is required for physical orders",
                    order.id(), false
            );
        }

        if (order.quantity() <= 0) {
            throw new OrderProcessingException(
                    "Quantity must be positive for physical orders",
                    order.id(), false
            );
        }
    }

    @Override
    public void visit(DigitalOrder order) {
        log.debug("Validating digital order {}", order.id());

        if (order.productCode() == null || order.productCode().isBlank()) {
            throw new OrderProcessingException(
                    "Product code is required for digital orders",
                    order.id(), false
            );
        }
    }

    @Override
    public void visit(TopUpOrder order) {
        log.debug("Validating topup order {}", order.id());

        if (order.walletId() == null || order.walletId().isBlank()) {
            throw new OrderProcessingException(
                    "Wallet ID is required for topup orders",
                    order.id(), false
            );
        }

        if (order.amountInCents() < TOPUP_MIN_AMOUNT) {
            throw new OrderProcessingException(
                    "TopUp amount must be at least " + TOPUP_MIN_AMOUNT + " cents",
                    order.id(), false
            );
        }
    }
}
