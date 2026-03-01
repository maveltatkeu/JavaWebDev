package com.company.order.application.usecase;

import com.company.order.application.factory.OrderStrategyFactory;
import com.company.order.domain.model.*;
import com.company.order.domain.port.in.CreateOrderCommand;
import com.company.order.domain.port.in.CreateOrderUseCase;
import com.company.order.domain.port.out.EventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

/**
 * IMPLÉMENTATION DU USE CASE – Service d'application central.
 *
 * Cette classe orchestre la création d'une commande :
 * 1. Génération d'un identifiant unique
 * 2. Instanciation du bon type d'Order (selon le type)
 * 3. Publication de l'événement ORDER_CREATED sur Kafka
 *
 * Le traitement réel (paiement, inventaire) est délégué au consumer Kafka
 * via les stratégies, permettant un découplage temporel.
 *
 * Appartient à la couche APPLICATION, pas au DOMAINE ni à l'INFRASTRUCTURE.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {

    private final EventPublisherPort eventPublisherPort;

    @Override
    public Mono<Order> createOrder(CreateOrderCommand command) {
        // Génération d'un ID unique pour la commande
        String orderId = UUID.randomUUID().toString();

        log.info("Creating order {} of type {} for customer {}",
                orderId, command.orderType(), command.customerId());

        // Construction de la commande selon son type
        Order order = buildOrder(orderId, command);

        // Publication asynchrone de l'événement de création
        // Le consumer Kafka prendra en charge le traitement
        return eventPublisherPort.publish(buildCreatedEvent(order))
                .thenReturn(order)
                .doOnSuccess(o -> log.info("Order {} created and event published", o.id()))
                .doOnError(e -> log.error("Failed to publish event for order {}: {}", orderId, e.getMessage()));
    }

    /**
     * Construit l'objet Order approprié selon le type.
     * Utilisation du pattern matching Java 17 (switch expression).
     */
    private Order buildOrder(String orderId, CreateOrderCommand command) {
        return switch (command.orderType()) {
            case PHYSICAL -> new PhysicalOrder(
                    orderId,
                    command.customerId(),
                    command.amountInCents(),
                    OrderStatus.PENDING,
                    command.correlationId(),
                    extractField(command.metadata(), "productSku"),
                    1,
                    extractField(command.metadata(), "shippingAddress")
            );
            case DIGITAL -> new DigitalOrder(
                    orderId,
                    command.customerId(),
                    command.amountInCents(),
                    OrderStatus.PENDING,
                    command.correlationId(),
                    extractField(command.metadata(), "licenseType"),
                    extractField(command.metadata(), "productCode")
            );
            case TOPUP -> new TopUpOrder(
                    orderId,
                    command.customerId(),
                    command.amountInCents(),
                    OrderStatus.PENDING,
                    command.correlationId(),
                    extractField(command.metadata(), "walletId")
            );
        };
    }

    private OrderEvent buildCreatedEvent(Order order) {
        return new OrderEvent(
                UUID.randomUUID().toString(),
                order.id(),
                order.type(),
                "ORDER_CREATED",
                order.correlationId(),
                Instant.now(),
                "{\"type\":\"" + order.type() + "\",\"customerId\":\"" + order.customerId() + "\"}"
        );
    }

    /**
     * Extraction simplifiée d'un champ depuis le metadata JSON.
     * En production, utiliser un ObjectMapper ou un record dédié par type.
     */
    private String extractField(String metadata, String field) {
        if (metadata == null) return "N/A";
        // Parsing simplifié – remplacer par Jackson en production
        String search = "\"" + field + "\":\"";
        int start = metadata.indexOf(search);
        if (start == -1) return "N/A";
        start += search.length();
        int end = metadata.indexOf("\"", start);
        return end > start ? metadata.substring(start, end) : "N/A";
    }
}
