package com.company.order.domain.model;

/**
 * Enumération des types de commandes supportés.
 * Chaque type suit un workflow métier différent et mobilise des services externes distincts.
 */
public enum OrderType {
    /** Produit physique nécessitant livraison et réservation d'inventaire */
    PHYSICAL,
    /** Licence ou produit numérique (SaaS, download) */
    DIGITAL,
    /** Recharge de crédit ou solde */
    TOPUP
}
