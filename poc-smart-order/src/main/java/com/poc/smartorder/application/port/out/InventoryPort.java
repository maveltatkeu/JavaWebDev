package com.poc.smartorder.application.port.out;

import com.poc.smartorder.domain.Order;
import reactor.core.publisher.Mono;

public interface InventoryPort {
    // Port pour la réservation de stock [cite: 84, 85]
    Mono<Void> reserve(Order order);
}
