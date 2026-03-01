package com.company.order.application.visitor;

import com.company.order.domain.model.DigitalOrder;
import com.company.order.domain.model.PhysicalOrder;
import com.company.order.domain.model.TopUpOrder;

/**
 * PATTERN VISITOR – Permet d'appliquer des règles sur les commandes
 * sans modifier les classes du domaine.
 *
 * Utilisation typique : validation métier, calcul de frais, règles fiscales.
 *
 * Avantage vs héritage : on peut ajouter des opérations indépendamment
 * des classes de données (Double Dispatch).
 */
public interface OrderVisitor {

    void visit(PhysicalOrder order);

    void visit(DigitalOrder order);

    void visit(TopUpOrder order);
}
