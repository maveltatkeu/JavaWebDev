package com.company.order.infrastructure.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * Configuration des WebClients pour les APIs externes.
 *
 * Un WebClient par service externe (payment, inventory, billing).
 * Configuration du timeout pour éviter les attentes indéfinies.
 */
@Configuration
public class WebClientConfig {

    @Value("${external.payment.base-url}")
    private String paymentBaseUrl;

    @Value("${external.inventory.base-url}")
    private String inventoryBaseUrl;

    @Value("${external.billing.base-url}")
    private String billingBaseUrl;

    @Bean
    public WebClient paymentWebClient() {
        return WebClient.builder()
                .baseUrl(paymentBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient inventoryWebClient() {
        return WebClient.builder()
                .baseUrl(inventoryBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient billingWebClient() {
        return WebClient.builder()
                .baseUrl(billingBaseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
