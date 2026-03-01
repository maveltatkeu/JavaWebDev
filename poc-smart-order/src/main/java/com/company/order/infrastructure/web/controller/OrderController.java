package com.company.order.infrastructure.web.controller;

import com.company.order.domain.port.in.CreateOrderCommand;
import com.company.order.domain.port.in.CreateOrderUseCase;
import com.company.order.infrastructure.web.dto.CreateOrderRequest;
import com.company.order.infrastructure.web.dto.CreateOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * ADAPTATEUR ENTRANT – REST Controller WebFlux.
 *
 * Point d'entrée HTTP du système. Traduit les requêtes HTTP en commandes domaine.
 *
 * Retourne HTTP 202 Accepted car le traitement est ASYNCHRONE :
 * la commande est créée et un événement Kafka est publié,
 * mais le traitement réel se fait dans le consumer.
 *
 * Aucune logique métier ici : uniquement mapping et délégation au UseCase.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    /**
     * Crée une nouvelle commande.
     *
     * HTTP 202 Accepted : traitement asynchrone via Kafka.
     * La commande est acceptée mais pas encore traitée.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        // Génère un correlationId si non fourni par le client
        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();

        log.info("POST /orders - type={}, customer={}, correlationId={}",
                request.orderType(), request.customerId(), corrId);

        // Mapping DTO → Command (isolation des couches)
        CreateOrderCommand command = new CreateOrderCommand(
                request.customerId(),
                request.orderType(),
                request.amountInCents(),
                corrId,
                request.metadata()
        );

        return createOrderUseCase.createOrder(command)
                .map(order -> new CreateOrderResponse(
                        order.id(),
                        order.type(),
                        order.status(),
                        order.correlationId(),
                        "Order accepted and queued for processing"
                ));
    }

    /** Health check simple */
    @GetMapping("/health")
    public Mono<String> health() {
        return Mono.just("Order service is running");
    }
}
