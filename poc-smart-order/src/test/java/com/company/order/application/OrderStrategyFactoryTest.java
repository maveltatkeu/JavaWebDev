package com.company.order.application;
import com.company.order.application.factory.OrderStrategyFactory;
import com.company.order.application.strategy.*;
import com.company.order.domain.model.OrderType;
import com.company.order.domain.port.out.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("OrderStrategyFactory – Dynamic Strategy Selection")
class OrderStrategyFactoryTest {
    private OrderStrategyFactory factory;

    @BeforeEach
    void setUp() {
        PaymentPort pp = mock(PaymentPort.class);
        InventoryPort ip = mock(InventoryPort.class);
        BillingPort bp = mock(BillingPort.class);
        EventPublisherPort ep = mock(EventPublisherPort.class);
        factory = new OrderStrategyFactory(List.of(
            new PhysicalOrderStrategy(pp, ip, ep),
            new DigitalOrderStrategy(pp, bp, ep),
            new TopUpOrderStrategy(pp, ep)
        ));
    }

    @ParameterizedTest
    @EnumSource(OrderType.class)
    @DisplayName("Should resolve strategy for every OrderType")
    void shouldResolveStrategyForAllTypes(OrderType type) {
        OrderProcessingStrategy strategy = factory.getStrategy(type);
        assertThat(strategy).isNotNull();
        assertThat(strategy.supportedType()).isEqualTo(type);
    }

    @Test
    void shouldReturnPhysicalStrategyForPhysical() {
        assertThat(factory.getStrategy(OrderType.PHYSICAL)).isInstanceOf(PhysicalOrderStrategy.class);
    }
}
