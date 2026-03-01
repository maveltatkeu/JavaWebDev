package com.company.order.domain.port.out;

import com.company.order.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * PORT SORTANT – Abstraction du service d'inventaire.
 *
 * Utilisé uniquement pour les commandes PHYSICAL.
 * Les commandes DIGITAL et TOPUP n'ont pas besoin de réserver du stock.
 */
public interface InventoryPort {

    /**
     * Réserve les articles nécessaires pour la commande.
     *
     * @param order commande physique à traiter
     * @return Mono<Void> complété quand la réservation est confirmée
     * @throws com.company.order.domain.exception.InsufficientStockException si stock insuffisant
     */
    Mono<Void> reserve(Order order);
}
