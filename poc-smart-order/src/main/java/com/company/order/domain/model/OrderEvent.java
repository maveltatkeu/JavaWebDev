package com.company.order.domain.model;

import java.time.Instant;

/**
 * Événement de domaine publié sur Kafka lors d'un changement d'état d'une commande.
 *
 * Cet objet est le contrat du message Kafka. Il doit rester stable pour garantir
 * la compatibilité entre producteurs et consommateurs.
 *
 * @param eventId       identifiant unique de l'événement (pour idempotence)
 * @param orderId       identifiant de la commande concernée
 * @param orderType     type de commande
 * @param eventType     type d'événement (ORDER_CREATED, ORDER_COMPLETED, ORDER_FAILED)
 * @param correlationId pour le tracing distribué
 * @param occurredAt    timestamp de l'événement
 * @param payload       données métier sérialisées en JSON
 */
public record OrderEvent(
        String eventId,
        String orderId,
        OrderType orderType,
        String eventType,
        String correlationId,
        Instant occurredAt,
        String payload
) {
  public static OrderEvent completed(Order order) {
    return null;
  }

  public static OrderEvent failed(Order order) {
    return null;
  }
}
