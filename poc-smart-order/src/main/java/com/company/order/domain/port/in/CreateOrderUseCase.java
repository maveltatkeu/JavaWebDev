package com.company.order.domain.port.in;

import com.company.order.domain.model.Order;
import reactor.core.publisher.Mono;

/**
 * PORT ENTRANT (Inbound Port) – Interface du cas d'utilisation principal.
 *
 * Dans l'architecture hexagonale, les ports entrants définissent ce que
 * le domaine expose à l'extérieur (REST, Kafka consumer, CLI...).
 *
 * Retourne un Mono<Order> pour rester non-bloquant (WebFlux).
 */
public interface CreateOrderUseCase {

    /**
     * Crée et initie le traitement d'une commande.
     *
     * @param command données de création de la commande
     * @return Mono contenant la commande créée (en état PENDING)
     */
    Mono<Order> createOrder(CreateOrderCommand command);
}
