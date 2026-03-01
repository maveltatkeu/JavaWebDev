package com.company.order.infrastructure.external.payment;

import com.company.order.domain.exception.PaymentDeclinedException;
import com.company.order.domain.model.Order;
import com.company.order.domain.model.PaymentResult;
import com.company.order.domain.port.out.PaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * ADAPTATEUR SORTANT – Implémentation Stripe du PaymentPort.
 *
 * Utilise Spring WebClient (non-bloquant) pour appeler l'API Stripe.
 * Le domaine n'a aucune connaissance de Stripe : il appelle PaymentPort.
 *
 * En cas d'erreur 402 (Payment Required) → PaymentDeclinedException (non-retryable)
 * En cas d'erreur 5xx (serveur) → RuntimeException (retryable via Kafka)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StripePaymentAdapter implements PaymentPort {

    private final WebClient paymentWebClient;

    @Override
    public Mono<PaymentResult> charge(Order order) {
        log.info("Calling Stripe API for order {} amount={}cents",
                order.id(), order.amountInCents());

        return paymentWebClient.post()
                .uri("/v1/charges")
                .bodyValue(Map.of(
                        "amount", order.amountInCents(),
                        "currency", "eur",
                        "metadata", Map.of(
                                "orderId", order.id(),
                                "correlationId", order.correlationId()
                        )
                ))
                .retrieve()
                // Gestion des erreurs HTTP spécifiques
                .onStatus(
                        status -> status.value() == 402,
                        response -> response.bodyToMono(String.class)
                                .map(body -> new PaymentDeclinedException(order.id(), body))
                )
                .bodyToMono(StripeChargeResponse.class)
                .map(response -> new PaymentResult(
                        response.id(),
                        "succeeded".equals(response.status()),
                        response.failureMessage()
                ))
                .doOnSuccess(r -> log.info("Payment result for order {}: success={}",
                        order.id(), r.success()));
    }

    /** DTO de réponse Stripe (simplifié) */
    record StripeChargeResponse(String id, String status, String failureMessage) {}
}
