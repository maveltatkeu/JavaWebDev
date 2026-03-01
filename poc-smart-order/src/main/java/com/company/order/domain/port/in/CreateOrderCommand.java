package com.company.order.domain.port.in;

import com.company.order.domain.model.OrderType;

/**
 * Commande (au sens CQRS) pour la création d'une commande métier.
 *
 * Objet immutable transportant toutes les données nécessaires au UseCase.
 * Séparation claire entre le DTO HTTP (couche infrastructure) et la commande domaine.
 *
 * @param customerId    identifiant du client
 * @param orderType     type de commande (PHYSICAL, DIGITAL, TOPUP)
 * @param amountInCents montant en centimes (évite les erreurs d'arrondi float)
 * @param correlationId identifiant de tracing propagé depuis la requête HTTP
 * @param metadata      données spécifiques au type (JSON sérialisé)
 */
public record CreateOrderCommand(
        String customerId,
        OrderType orderType,
        long amountInCents,
        String correlationId,
        String metadata
) {}
