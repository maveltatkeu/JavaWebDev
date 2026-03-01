package com.company.order.infrastructure.web.dto;

import com.company.order.domain.model.OrderResult;
import com.company.order.domain.model.OrderStatus;

import java.time.Instant;

/**
 * DTO de réponse HTTP pour la création d'une commande.
 *
 * Retourné avec un statut HTTP 202 ACCEPTED car le traitement est asynchrone.
 */
public record OrderResponse(
        String orderId,
        OrderStatus status,
        String message,
        Instant acceptedAt
) {
    /**
     * Crée une réponse à partir du résultat du use case.
     */
    public static OrderResponse from(OrderResult result) {
        return new OrderResponse(
                result.orderId(),
                result.status(),
                result.message(),
                result.processedAt()
        );
    }
}
