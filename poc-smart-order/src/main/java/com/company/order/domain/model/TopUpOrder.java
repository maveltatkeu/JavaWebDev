package com.company.order.domain.model;

/**
 * Commande de recharge : crédite un solde sans réservation d'inventaire.
 *
 * @param id            identifiant unique
 * @param customerId    identifiant client
 * @param amountInCents montant à recharger
 * @param status        statut courant
 * @param correlationId identifiant de tracing
 * @param walletId      identifiant du portefeuille à créditer
 */
public record TopUpOrder(
        String id,
        String customerId,
        long amountInCents,
        OrderStatus status,
        String correlationId,
        String walletId
) implements Order {

    @Override
    public OrderType type() {
        return OrderType.TOPUP;
    }
}
