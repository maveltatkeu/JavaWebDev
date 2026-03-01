package com.company.order.domain.port.out;

import com.company.order.domain.model.Order;
import com.company.order.domain.model.PaymentResult;
import reactor.core.publisher.Mono;

/**
 * PORT SORTANT (Outbound Port) – Abstraction du service de paiement.
 *
 * Le domaine ne connaît que cette interface, jamais Stripe/PayPal/etc.
 * L'implémentation concrète (StripePaymentAdapter) vit en infrastructure.
 *
 * Avantage : facile à mocker en tests, facile à remplacer le provider.
 */
public interface PaymentPort {

    /**
     * Débite le client pour la commande donnée.
     *
     * @param order commande à facturer
     * @return résultat du paiement (succès ou échec avec détails)
     */
    Mono<PaymentResult> charge(Order order);
}
