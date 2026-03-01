package com.company.order.domain.exception;

/**
 * Erreur PERMANENTE : stock insuffisant. Aucun retry ne résoudra ce problème.
 */
public class InsufficientStockException extends OrderProcessingException {
    public InsufficientStockException(String orderId, String sku) {
        super("Insufficient stock for SKU " + sku + " on order " + orderId, orderId, false);
    }
}
