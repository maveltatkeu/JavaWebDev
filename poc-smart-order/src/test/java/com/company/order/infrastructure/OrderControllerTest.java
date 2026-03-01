package com.company.order.infrastructure;
import com.company.order.domain.model.*;
import com.company.order.domain.port.in.*;
import com.company.order.infrastructure.web.controller.OrderController;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests de la couche Web (REST Controller WebFlux).
 *
 * @WebFluxTest charge uniquement la couche web sans le contexte Kafka.
 * Le UseCase est mocke pour isoler le test du controller.
 */
@WebFluxTest(OrderController.class)
@DisplayName("OrderController – REST Layer Tests")
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @Test
    @DisplayName("POST /orders should return 202 Accepted")
    void shouldReturn202OnValidRequest() {
        Order mockOrder = new PhysicalOrder("ord-001", "cust-001", 5000L,
            OrderStatus.PENDING, "corr-001", "SKU-1", 1, "Paris");

        when(createOrderUseCase.createOrder(any(CreateOrderCommand.class)))
            .thenReturn(Mono.just(mockOrder));

        webTestClient.post()
            .uri("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Correlation-ID", "test-corr-001")
            .bodyValue("""
                {
                  "customerId": "cust-001",
                  "orderType": "PHYSICAL",
                  "amountInCents": 5000,
                  "metadata": "{\"productSku\":\"SKU-1\",\"shippingAddress\":\"Paris\"}"
                }
                """)
            .exchange()
            .expectStatus().isAccepted()
            .expectBody()
            .jsonPath("$.orderId").isEqualTo("ord-001")
            .jsonPath("$.status").isEqualTo("PENDING");
    }

    @Test
    @DisplayName("POST /orders without customerId should return 400")
    void shouldReturn400WhenCustomerIdMissing() {
        webTestClient.post()
            .uri("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("""
                {
                  "orderType": "PHYSICAL",
                  "amountInCents": 5000
                }
                """)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("GET /orders/health should return 200")
    void healthEndpointShouldReturn200() {
        webTestClient.get()
            .uri("/api/v1/orders/health")
            .exchange()
            .expectStatus().isOk();
    }
}
