package com.company.order.infrastructure.kafka.config;

/**
 * Constantes des topics Kafka.
 *
 * Centraliser les noms de topics évite les erreurs de typo
 * et facilite la refactorisation.
 *
 * Topics :
 * - ORDER_CREATED    : nouvelle commande reçue (source)
 * - ORDER_PROCESSING : traitement en cours (informatif)
 * - ORDER_COMPLETED  : traitement terminé avec succès
 * - ORDER_FAILED     : traitement échoué (après DLQ)
 * - ORDER_CREATED_DLT: Dead Letter Topic pour les messages non traités
 */
public final class KafkaTopics {

    private KafkaTopics() { /* utility class */ }

    public static final String ORDER_CREATED    = "order.created";
    public static final String ORDER_PROCESSING = "order.processing";
    public static final String ORDER_COMPLETED  = "order.completed";
    public static final String ORDER_FAILED     = "order.failed";
    public static final String ORDER_CREATED_DLT = "order.created.DLT";
}
