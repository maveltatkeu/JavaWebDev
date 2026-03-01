package com.company.order.bdd.config;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.company.order.domain.port.out.*;

/**
 * Configuration Spring pour les tests Cucumber BDD.
 *
 * @CucumberContextConfiguration : partage le contexte Spring entre les step definitions.
 * @SpringBootTest : charge le contexte complet (sauf Kafka = mocke).
 */
@CucumberContextConfiguration
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.kafka.bootstrap-servers=localhost:9999", // Kafka desactive en BDD
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
    }
)
public class CucumberSpringConfig {

    // Les ports sortants sont mockes pour isoler les tests BDD
    @MockBean public PaymentPort paymentPort;
    @MockBean public InventoryPort inventoryPort;
    @MockBean public BillingPort billingPort;
    @MockBean public EventPublisherPort eventPublisherPort;
}
