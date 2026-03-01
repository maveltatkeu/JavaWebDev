package com.company.order.application;
import com.company.order.application.usecase.CreateOrderService;
import com.company.order.domain.model.*;
import com.company.order.domain.port.in.CreateOrderCommand;
import com.company.order.domain.port.out.EventPublisherPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests du UseCase principal de création de commande */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateOrderService – UseCase Tests")
class CreateOrderServiceTest {
    @Mock EventPublisherPort eventPublisherPort;
    private CreateOrderService service;

    @BeforeEach
    void setUp() { service = new CreateOrderService(eventPublisherPort); }

    @Test
    @DisplayName("Physical order is created with PENDING status")
    void shouldCreatePhysicalOrderAsPending() {
        when(eventPublisherPort.publish(any())).thenReturn(Mono.empty());
        CreateOrderCommand cmd = new CreateOrderCommand("cust-001", OrderType.PHYSICAL, 5000L,
            "corr-001", "{\"productSku\":\"SKU-A\",\"shippingAddress\":\"Paris\"}");

        StepVerifier.create(service.createOrder(cmd))
            .assertNext(order -> {
                assertThat(order).isInstanceOf(PhysicalOrder.class);
                assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
                assertThat(order.id()).isNotBlank();
            })
            .verifyComplete();

        verify(eventPublisherPort).publish(argThat(e -> "ORDER_CREATED".equals(e.eventType())));
    }

    @Test
    @DisplayName("Should fail reactively when Kafka is unavailable")
    void shouldFailWhenKafkaUnavailable() {
        when(eventPublisherPort.publish(any())).thenReturn(Mono.error(new RuntimeException("Kafka down")));

        StepVerifier.create(service.createOrder(
            new CreateOrderCommand("c-1", OrderType.TOPUP, 500L, "corr", "{\"walletId\":\"w1\"}")
        )).expectError(RuntimeException.class).verify();
    }
}
