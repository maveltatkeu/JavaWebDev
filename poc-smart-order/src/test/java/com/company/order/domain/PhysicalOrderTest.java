package com.company.order.domain;
import com.company.order.domain.model.*;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Domain Model – PhysicalOrder")
class PhysicalOrderTest {

    @Test
    @DisplayName("Should return PHYSICAL type")
    void shouldReturnPhysicalType() {
        assertThat(createSampleOrder().type()).isEqualTo(OrderType.PHYSICAL);
    }

    @Test
    @DisplayName("Records should be value-equal (Java 17)")
    void recordsShouldBeValueEqual() {
        assertThat(createSampleOrder()).isEqualTo(createSampleOrder());
    }

    @Test
    @DisplayName("Sealed classes enable exhaustive pattern matching")
    void sealedClassesEnablePatternMatching() {
        Order order = createSampleOrder();
        // Compilateur Java 17 garantit l exhaustivite
        String result = switch (order) {
            case PhysicalOrder p -> "physical";
            case DigitalOrder d -> "digital";
            case TopUpOrder t -> "topup";
        };
        assertThat(result).isEqualTo("physical");
    }

    @Test
    @DisplayName("Status should be PENDING on creation")
    void statusShouldBePendingOnCreation() {
        assertThat(createSampleOrder().status()).isEqualTo(OrderStatus.PENDING);
    }

    private PhysicalOrder createSampleOrder() {
        return new PhysicalOrder("order-123", "cust-456", 9999L,
                OrderStatus.PENDING, "corr-789", "SKU-001", 2, "123 Main St");
    }
}
