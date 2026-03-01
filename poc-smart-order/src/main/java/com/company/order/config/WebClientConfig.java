package com.company.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration des WebClients pour les services externes.
 * Chaque service externe a son propre WebClient avec sa base URL.
 * En production : ajouter timeout, retry, circuit breaker (Resilience4j).
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient paymentWebClient(@Value("${external.payment.base-url:http://localhost:8081}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json").build();
    }

    @Bean
    public WebClient inventoryWebClient(@Value("${external.inventory.base-url:http://localhost:8082}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json").build();
    }

    @Bean
    public WebClient billingWebClient(@Value("${external.billing.base-url:http://localhost:8083}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json").build();
    }
}
