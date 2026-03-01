package com.company.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Point d'entrée du microservice Smart Order Orchestrator.
 * Architecture : Hexagonale (Ports & Adapters)
 * Paradigme  : Event-driven + Réactif (WebFlux)
 * Résilience  : Retry exponentiel + DLQ (Kafka)
 */
@SpringBootApplication
@EnableKafka
@EnableRetry
public class SmartOrderOrchestratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartOrderOrchestratorApplication.class, args);
    }
}
