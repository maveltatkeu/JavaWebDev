package com.company.order.domain.model;

/**
 * Commande digitale : paiement uniquement, pas de stock physique.
 * Génère une clé de licence après validation.
 *
 * @param id            identifiant unique
 * @param customerId    identifiant client
 * @param amountInCents montant en centimes
 * @param status        statut courant
 * @param correlationId identifiant de tracing
 * @param licenseType   type de licence (ex: ANNUAL, MONTHLY)
 * @param productCode   code produit numérique
 */
public record DigitalOrder(
        String id,
        String customerId,
        long amountInCents,
        OrderStatus status,
        String correlationId,
        String licenseType,
        String productCode
) implements Order {

    @Override
    public OrderType type() {
        return OrderType.DIGITAL;
    }
}
