package com.company.order.domain.model;

/**
 * Commande physique : implique un paiement ET une réservation d'inventaire.
 *
 * Record Java 17 : immutable, equals/hashCode/toString générés automatiquement.
 *
 * @param id            identifiant unique
 * @param customerId    identifiant client
 * @param amountInCents montant en centimes
 * @param status        statut courant
 * @param correlationId identifiant de tracing distribué
 * @param productSku    référence du produit à livrer
 * @param quantity      quantité commandée
 * @param shippingAddress adresse de livraison
 */
public record PhysicalOrder(
        String id,
        String customerId,
        long amountInCents,
        OrderStatus status,
        String correlationId,
        String productSku,
        int quantity,
        String shippingAddress
) implements Order {

    @Override
    public OrderType type() {
        return OrderType.PHYSICAL;
    }
}
