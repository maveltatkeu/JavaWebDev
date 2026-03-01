package com.company.order.domain.exception;

/**
 * Erreur PERMANENTE : carte refusée ou fonds insuffisants.
 * Ne doit pas déclencher de retry → envoi direct en DLQ.
 */
public class PaymentDeclinedException extends OrderProcessingException {
    public PaymentDeclinedException(String orderId, String reason) {
        super("Payment declined for order " + orderId + ": " + reason, orderId, false);
    }
}
