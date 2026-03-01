package com.company.order.domain.model;

/**
 * Types d'événements publiés sur les topics Kafka.
 *
 * Chaque type correspond à un topic dédié :
 * - ORDER_CREATED    → order.created
 * - ORDER_PROCESSING → order.processing
 * - ORDER_COMPLETED  → order.completed
 * - ORDER_FAILED     → order.failed
 */
public enum EventType {
    ORDER_CREATED,
    ORDER_PROCESSING,
    ORDER_COMPLETED,
    ORDER_FAILED
}
