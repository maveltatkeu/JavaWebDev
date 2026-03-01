package com.company.order.infrastructure.web.dto;

import com.company.order.domain.model.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO HTTP entrant – Données de la requête POST /orders.
 *
 * Séparé de CreateOrderCommand (domaine) pour isoler la couche HTTP.
 * Les annotations @Valid appartiennent à la couche HTTP, pas au domaine.
 */
public record CreateOrderRequest(

        @NotNull(message = "customerId is required")
        String customerId,

        @NotNull(message = "orderType is required")
        OrderType orderType,

        @Min(value = 1, message = "Amount must be at least 1 cent")
        long amountInCents,

        /** Données spécifiques au type, passées en JSON stringifié */
        String metadata
) {}
