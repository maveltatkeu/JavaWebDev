package com.company.order.domain.exception;

/**
 * Exception de base pour les erreurs de traitement de commande.
 * - Erreurs TRANSITOIRES (réseau, timeout) → retry exponentiel possible
 * - Erreurs PERMANENTES (carte refusée, données invalides) → DLQ directe
 */
public class OrderProcessingException extends RuntimeException {
    private final boolean retryable;
    private final String orderId;

    public OrderProcessingException(String message, String orderId, boolean retryable) {
        super(message);
        this.retryable = retryable;
        this.orderId = orderId;
    }

    public OrderProcessingException(String message, String orderId, boolean retryable, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
        this.orderId = orderId;
    }

    public boolean isRetryable() { return retryable; }
    public String getOrderId() { return orderId; }
}
