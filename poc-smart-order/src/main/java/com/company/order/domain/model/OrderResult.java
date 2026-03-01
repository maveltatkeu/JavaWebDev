package com.company.order.domain.model;

import java.time.Instant;

/**
 * Résultat de l'orchestration d'une commande.
 * Objet valeur immuable retourné par le use case.
 */
public record OrderResult(
        String orderId,
        OrderStatus status,
        String message,
        Instant processedAt
) {

    public static OrderResult success(String orderId) {
        return new OrderResult(orderId, OrderStatus.ACCEPTED, "Order accepted", Instant.now());
    }

    public static OrderResult failure(String orderId, String reason) {
        return new OrderResult(orderId, OrderStatus.REJECTED, reason, Instant.now());
    }
}
