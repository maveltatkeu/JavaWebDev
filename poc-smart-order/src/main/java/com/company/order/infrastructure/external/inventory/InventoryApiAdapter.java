package com.company.order.infrastructure.external.inventory;

import com.company.order.domain.exception.InsufficientStockException;
import com.company.order.domain.model.Order;
import com.company.order.domain.model.PhysicalOrder;
import com.company.order.domain.port.out.InventoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * ADAPTATEUR SORTANT – Implémentation du service d'inventaire.
 *
 * Appelle un micro-service d'inventaire externe via WebClient.
 * En cas de stock insuffisant (409 Conflict) → InsufficientStockException (non-retryable)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryApiAdapter implements InventoryPort {

    private final WebClient inventoryWebClient;

    @Override
    public Mono<Void> reserve(Order order) {
        if (!(order instanceof PhysicalOrder physicalOrder)) {
            // Seules les commandes physiques nécessitent une réservation
            return Mono.empty();
        }

        log.info("Reserving {} x {} for order {}",
                physicalOrder.quantity(), physicalOrder.productSku(), order.id());

        return inventoryWebClient.post()
                .uri("/v1/reservations")
                .bodyValue(Map.of(
                        "sku", physicalOrder.productSku(),
                        "quantity", physicalOrder.quantity(),
                        "orderId", order.id()
                ))
                .retrieve()
                .onStatus(
                        status -> status.value() == 409,
                        response -> Mono.error(new InsufficientStockException(
                                order.id(), physicalOrder.productSku()
                        ))
                )
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Inventory reserved for order {}", order.id()));
    }
}
