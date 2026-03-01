package com.company.order.infrastructure.web.dto;

import com.company.order.domain.model.OrderStatus;
import com.company.order.domain.model.OrderType;

/**
 * DTO HTTP sortant – Réponse de la création de commande.
 * Retourné avec HTTP 202 Accepted (traitement asynchrone).
 */
public record CreateOrderResponse(
        String orderId,
        OrderType orderType,
        OrderStatus status,
        String correlationId,
        String message
) {}
