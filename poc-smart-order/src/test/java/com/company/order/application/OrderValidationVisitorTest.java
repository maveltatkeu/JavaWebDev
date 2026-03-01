package com.company.order.application;
import com.company.order.application.visitor.OrderValidationVisitor;
import com.company.order.domain.exception.OrderProcessingException;
import com.company.order.domain.model.*;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

/** Tests du Visitor de validation - verifie les regles metier par type */
@DisplayName("OrderValidationVisitor – Business Rules")
class OrderValidationVisitorTest {

    private final OrderValidationVisitor visitor = new OrderValidationVisitor();

    @Test
    @DisplayName("Physical order with address and quantity > 0 should be valid")
    void physicalOrderWithAllDataIsValid() {
        PhysicalOrder order = new PhysicalOrder("o-1", "c-1", 1000L,
            OrderStatus.PENDING, "corr", "SKU-1", 2, "123 Rue de la Paix");
        assertThatNoException().isThrownBy(() -> visitor.visit(order));
    }

    @Test
    @DisplayName("Physical order without shipping address should fail")
    void physicalOrderWithoutAddressShouldFail() {
        PhysicalOrder order = new PhysicalOrder("o-1", "c-1", 1000L,
            OrderStatus.PENDING, "corr", "SKU-1", 2, "");
        assertThatThrownBy(() -> visitor.visit(order))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessageContaining("Shipping address");
    }

    @Test
    @DisplayName("Physical order with quantity 0 should fail")
    void physicalOrderWithZeroQuantityShouldFail() {
        PhysicalOrder order = new PhysicalOrder("o-1", "c-1", 1000L,
            OrderStatus.PENDING, "corr", "SKU-1", 0, "Paris");
        assertThatThrownBy(() -> visitor.visit(order))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessageContaining("Quantity");
    }

    @Test
    @DisplayName("Digital order without product code should fail")
    void digitalOrderWithoutProductCodeShouldFail() {
        DigitalOrder order = new DigitalOrder("o-2", "c-1", 2000L,
            OrderStatus.PENDING, "corr", "ANNUAL", "");
        assertThatThrownBy(() -> visitor.visit(order))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessageContaining("Product code");
    }

    @Test
    @DisplayName("TopUp below minimum amount should fail")
    void topUpBelowMinimumShouldFail() {
        TopUpOrder order = new TopUpOrder("o-3", "c-1", 50L,
            OrderStatus.PENDING, "corr", "wallet-1");
        assertThatThrownBy(() -> visitor.visit(order))
            .isInstanceOf(OrderProcessingException.class)
            .hasMessageContaining("at least");
    }
}
