package com.company.order.application.usecase;

import com.company.order.domain.model.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Commande d'entrée (Command Object) pour la création d'une commande.
 *
 * Représente la demande du client avant toute logique métier.
 * Les annotations de validation sont acceptables ici car ce record
 * est dans la couche application (pas le domaine pur).
 *
 * Converti en entité Order par le OrderMapper.
 */
public record CreateOrderCommand(

        @NotBlank(message = "customerId is required")
        String customerId,

        @NotNull(message = "orderType is required")
        OrderType orderType,

        @Min(value = 1, message = "amount must be positive")
        long amountInCents,

        // Champs optionnels selon le type de commande
        String deliveryAddress,  // PHYSICAL seulement
        String productSku,       // PHYSICAL seulement
        Integer quantity,        // PHYSICAL seulement
        String licenseKey,       // DIGITAL seulement
        String productCode,      // DIGITAL seulement
        String activationEmail,  // DIGITAL seulement
        String accountNumber,    // TOPUP seulement
        String provider          // TOPUP seulement
) {}
