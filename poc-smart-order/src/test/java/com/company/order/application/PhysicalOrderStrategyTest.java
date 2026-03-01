package com.company.order.application;
import com.company.order.application.strategy.PhysicalOrderStrategy;
import com.company.order.domain.exception.PaymentDeclinedException;
import com.company.order.domain.model.*;
import com.company.order.domain.port.out.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Tests unitaires de la PhysicalOrderStrategy avec Mockito + StepVerifier */
@ExtendWith(MockitoExtension.class)
@DisplayName("PhysicalOrderStrategy – Unit Tests")
class PhysicalOrderStrategyTest {
    @Mock PaymentPort paymentPort;
    @Mock InventoryPort inventoryPort;
    @Mock EventPublisherPort eventPublisherPort;
    private PhysicalOrderStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PhysicalOrderStrategy(paymentPort, inventoryPort, eventPublisherPort);
    }

    @Test
    @DisplayName("Full success: payment + inventory + event published")
    void shouldCompleteFullPipelineOnSuccess() {
        PhysicalOrder order = sample();
        when(paymentPort.charge(any())).thenReturn(Mono.just(new PaymentResult("txn-1", true, null)));
        when(inventoryPort.reserve(any())).thenReturn(Mono.empty());
        when(eventPublisherPort.publish(any())).thenReturn(Mono.empty());

        StepVerifier.create(strategy.process(order)).verifyComplete();

        InOrder inOrder = inOrder(paymentPort, inventoryPort, eventPublisherPort);
        inOrder.verify(paymentPort).charge(order);
        inOrder.verify(inventoryPort).reserve(order);
        inOrder.verify(eventPublisherPort).publish(any());
    }

    @Test
    @DisplayName("Payment declined: inventory and event must NOT be called")
    void shouldShortCircuitOnPaymentDeclined() {
        PhysicalOrder order = sample();
        when(paymentPort.charge(any()))
            .thenReturn(Mono.error(new PaymentDeclinedException(order.id(), "no funds")));

        StepVerifier.create(strategy.process(order))
            .expectError(PaymentDeclinedException.class)
            .verify();

        verify(inventoryPort, never()).reserve(any());
        verify(eventPublisherPort, never()).publish(any());
    }

    @Test
    @DisplayName("Inventory failure: event must NOT be published")
    void shouldNotPublishEventOnInventoryFailure() {
        PhysicalOrder order = sample();
        when(paymentPort.charge(any())).thenReturn(Mono.just(new PaymentResult("txn-1", true, null)));
        when(inventoryPort.reserve(any())).thenReturn(Mono.error(new RuntimeException("timeout")));

        StepVerifier.create(strategy.process(order))
            .expectError(RuntimeException.class)
            .verify();

        verify(eventPublisherPort, never()).publish(any());
    }

    @Test
    @DisplayName("Published event must have ORDER_COMPLETED type and correct orderId")
    void shouldPublishCorrectEvent() {
        PhysicalOrder order = sample();
        when(paymentPort.charge(any())).thenReturn(Mono.just(new PaymentResult("txn-1", true, null)));
        when(inventoryPort.reserve(any())).thenReturn(Mono.empty());
        when(eventPublisherPort.publish(any())).thenReturn(Mono.empty());

        StepVerifier.create(strategy.process(order)).verifyComplete();

        verify(eventPublisherPort).publish(argThat(e ->
            "ORDER_COMPLETED".equals(e.eventType()) && order.id().equals(e.orderId())
        ));
    }

    private PhysicalOrder sample() {
        return new PhysicalOrder("ord-123", "cust-456", 9999L,
            OrderStatus.PENDING, "corr-789", "SKU-001", 2, "Paris");
    }
}
