package com.company.order.domain.model;

/**
 * Cycle de vie d'une commande.
 * Les transitions valides sont : PENDING → PROCESSING → COMPLETED | FAILED
 */
public enum OrderStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
