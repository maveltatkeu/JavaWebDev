package com.company.order.domain.port.out;

import com.company.order.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * PORT SORTANT – Abstraction du service de facturation.
 *
 * Génère la facture après validation du paiement.
 * Utilisé par les commandes DIGITAL et PHYSICAL.
 */
public interface BillingPort {

    /**
     * Génère et envoie une facture pour la commande.
     *
     * @param order commande facturée
     * @return identifiant de la facture générée
     */
    Mono<String> generateInvoice(Order order);
}
