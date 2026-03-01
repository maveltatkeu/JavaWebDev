package com.company.order.application.strategy;

import com.company.order.domain.model.Order;
import com.company.order.domain.model.OrderType;
import reactor.core.publisher.Mono;

/**
 * PATTERN STRATEGY – Interface commune pour le traitement de toutes les commandes.
 *
 * Chaque type de commande (PHYSICAL, DIGITAL, TOPUP) possède sa propre implémentation.
 * La sélection se fait dynamiquement via la OrderStrategyFactory.
 *
 * Avantage : ajouter un nouveau type de commande = créer une nouvelle stratégie
 * sans modifier le code existant (Open/Closed Principle).
 */
public interface OrderProcessingStrategy {

    /**
     * Exécute le pipeline de traitement complet pour la commande.
     * Retourne un Mono<Void> : non-bloquant, pas de valeur de retour.
     *
     * @param order commande à traiter
     * @return Mono<Void> complété après traitement, ou en erreur si échec
     */
    Mono<Void> process(Order order);

    /**
     * Indique le type de commande que cette stratégie traite.
     * Utilisé par la factory pour la sélection dynamique.
     */
    OrderType supportedType();
}
