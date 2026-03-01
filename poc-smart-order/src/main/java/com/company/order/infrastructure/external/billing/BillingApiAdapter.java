package com.company.order.infrastructure.external.billing;

import com.company.order.domain.model.Order;
import com.company.order.domain.port.out.BillingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Map;

/**
 * ADAPTATEUR SORTANT – Génération de factures via service externe.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BillingApiAdapter implements BillingPort {

    private final WebClient billingWebClient;

    @Override
    public Mono<String> generateInvoice(Order order) {
        log.info("Generating invoice for order {}", order.id());
        return billingWebClient.post()
                .uri("/v1/invoices")
                .bodyValue(Map.of(
                        "orderId", order.id(),
                        "customerId", order.customerId(),
                        "amountInCents", order.amountInCents()
                ))
                .retrieve()
                .bodyToMono(InvoiceResponse.class)
                .map(InvoiceResponse::invoiceId)
                .doOnSuccess(id -> log.info("Invoice {} generated for order {}", id, order.id()));
    }

    record InvoiceResponse(String invoiceId, String status) {}
}
