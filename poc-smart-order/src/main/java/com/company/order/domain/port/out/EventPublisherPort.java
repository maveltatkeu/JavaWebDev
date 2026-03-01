package com.company.order.domain.port.out;

import com.company.order.domain.model.OrderEvent;
import reactor.core.publisher.Mono;

/**
 * PORT SORTANT – Abstraction du bus d'événements.
 *
 * Le domaine publie des événements sans savoir que c'est Kafka derrière.
 * Cela permet de tester la logique métier avec un publisher en mémoire.
 */
public interface EventPublisherPort {

    /**
     * Publie un événement de domaine sur le bus de messages.
     *
     * @param event événement à publier
     * @return Mono<Void> complété quand l'événement est envoyé
     */
    Mono<Void> publish(OrderEvent event);
}
