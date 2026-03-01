package com.company.order.bdd.steps;
import com.company.order.domain.model.*;
import com.company.order.domain.port.in.CreateOrderCommand;
import com.company.order.domain.port.in.CreateOrderUseCase;
import com.company.order.domain.port.out.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * STEP DEFINITIONS BDD – Implémentation des scenarios Cucumber en Java.
 *
 * Chaque methode correspond a une etape (Given/When/Then) d un scenario .feature.
 * Le langage Gherkin rend les tests lisibles par des non-developpeurs (PO, QA).
 */
public class OrderProcessingSteps {

    @Autowired private CreateOrderUseCase createOrderUseCase;
    @Autowired private PaymentPort paymentPort;
    @Autowired private InventoryPort inventoryPort;
    @Autowired private BillingPort billingPort;
    @Autowired private EventPublisherPort eventPublisherPort;

    private CreateOrderCommand currentCommand;
    private Order createdOrder;
    private Exception thrownException;

    @Before
    void resetMocks() {
        reset(paymentPort, inventoryPort, billingPort, eventPublisherPort);
    }

    // ========================= GIVEN =========================

    @Given("a valid physical order with amount {long} cents")
    public void aValidPhysicalOrderWithAmount(long amountInCents) {
        currentCommand = new CreateOrderCommand(
            "customer-bdd", OrderType.PHYSICAL, amountInCents, "bdd-corr",
            "{\"productSku\":\"SKU-BDD\",\"shippingAddress\":\"Paris BDD\"}"
        );
    }

    @Given("a valid digital order")
    public void aValidDigitalOrder() {
        currentCommand = new CreateOrderCommand(
            "customer-bdd", OrderType.DIGITAL, 2000L, "bdd-corr",
            "{\"productCode\":\"PROD-BDD\",\"licenseType\":\"ANNUAL\"}"
        );
    }

    @Given("a valid topup order")
    public void aValidTopupOrder() {
        currentCommand = new CreateOrderCommand(
            "customer-bdd", OrderType.TOPUP, 1500L, "bdd-corr",
            "{\"walletId\":\"wallet-bdd\"}"
        );
    }

    @Given("the payment service is available")
    public void thePaymentServiceIsAvailable() {
        when(paymentPort.charge(any()))
            .thenReturn(Mono.just(new PaymentResult("txn-bdd", true, null)));
    }

    @Given("the payment service is unavailable")
    public void thePaymentServiceIsUnavailable() {
        when(paymentPort.charge(any()))
            .thenReturn(Mono.error(new RuntimeException("payment service down")));
    }

    @Given("the inventory service is available")
    public void theInventoryServiceIsAvailable() {
        when(inventoryPort.reserve(any())).thenReturn(Mono.empty());
    }

    @Given("the event publisher is available")
    public void theEventPublisherIsAvailable() {
        when(eventPublisherPort.publish(any())).thenReturn(Mono.empty());
    }

    // ========================= WHEN =========================

    @When("the order is submitted")
    public void theOrderIsSubmitted() {
        try {
            createdOrder = createOrderUseCase.createOrder(currentCommand).block();
        } catch (Exception e) {
            thrownException = e;
        }
    }

    // ========================= THEN =========================

    @Then("the order should be created with status PENDING")
    public void theOrderShouldBeCreatedWithStatusPending() {
        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Then("the order ID should be generated")
    public void theOrderIdShouldBeGenerated() {
        assertThat(createdOrder.id()).isNotBlank();
    }

    @Then("an ORDER_CREATED event should be published")
    public void anOrderCreatedEventShouldBePublished() {
        verify(eventPublisherPort).publish(argThat(e -> "ORDER_CREATED".equals(e.eventType())));
    }

    @Then("the order should fail with an error")
    public void theOrderShouldFailWithAnError() {
        assertThat(thrownException).isNotNull();
    }

    @Then("it should be a {word} order")
    public void itShouldBeATypeOrder(String type) {
        assertThat(createdOrder.type().name()).isEqualToIgnoringCase(type);
    }
}
