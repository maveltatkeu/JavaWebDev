package com.company.order.domain.model;

/**
 * Interface scellée représentant une commande dans le domaine métier.
 *
 * Utilisation des sealed classes Java 17 pour garantir l'exhaustivité
 * lors des pattern matching (switch expressions) sans risque d'oubli de cas.
 *
 * IMPORTANT : Cette interface n'a aucune dépendance vers Spring ou Kafka.
 * Elle appartient exclusivement au domaine (core hexagonal).
 */
public sealed interface Order permits PhysicalOrder, DigitalOrder, TopUpOrder {

    /** Identifiant unique de la commande (UUID sous forme de String) */
    String id();

    /** Identifiant du client passant la commande */
    String customerId();

    /** Montant de la commande en centimes */
    long amountInCents();

    /** Statut courant de la commande */
    OrderStatus status();

    /** Type de la commande – détermine la stratégie de traitement */
    OrderType type();

    /** Identifiant de corrélation pour le tracing distribué */
    String correlationId();
}
