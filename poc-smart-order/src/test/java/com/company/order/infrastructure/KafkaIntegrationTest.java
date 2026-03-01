package com.company.order.infrastructure;

import com.company.order.application.usecase.CreateOrderService;
import com.company.order.domain.model.OrderType;
import com.company.order.domain.port.in.CreateOrderCommand;
import com.company.order.domain.port.out.EventPublisherPort;
import com.company.order.infrastructure.kafka.producer.KafkaEventPublisher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TEST D'INTÉGRATION KAFKA avec EmbeddedKafka.
 *
 * EmbeddedKafka démarre un broker Kafka en mémoire pour les tests.
 * Plus rapide que Testcontainers, idéal pour les tests CI/CD.
 *
 * Ce test vérifie le flow complet :
 * CreateOrderService → KafkaEventPublisher → EmbeddedKafka Broker
 */
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"},
    topics = {"order.created.test", "order.completed.test"}
)
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "kafka.topics.order-created=order.created.test",
    "kafka.topics.order-completed=order.completed.test",
    "kafka.consumer.group-id=test-group"
})
@DisplayName("Kafka Integration – EmbeddedKafka")
class KafkaIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private EventPublisherPort eventPublisherPort;

    private BlockingQueue<ConsumerRecord<String, String>> receivedMessages;
    private KafkaMessageListenerContainer<String, String> container;

    @BeforeEach
    void setUp() {
        receivedMessages = new LinkedBlockingQueue<>();

        // Configuration d'un consumer de test
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
            "test-group", "true", embeddedKafkaBroker
        );
        DefaultKafkaConsumerFactory<String, String> consumerFactory =
            new DefaultKafkaConsumerFactory<>(consumerProps);

        ContainerProperties containerProperties = new ContainerProperties("order.created.test");
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        // Listener qui collecte les messages dans la queue pour assertion
        container.setupMessageListener((MessageListener<String, String>) record ->
            receivedMessages.add(record)
        );
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @AfterEach
    void tearDown() {
        container.stop();
    }

    @Test
    @DisplayName("Event published should be received by consumer")
    void publishedEventShouldBeReceivedByConsumer() throws InterruptedException {
        // ARRANGE : créer et publier un événement
        CreateOrderService service = new CreateOrderService(eventPublisherPort);
        CreateOrderCommand command = new CreateOrderCommand(
            "cust-kafka", OrderType.TOPUP, 500L, "corr-kafka",
            "{\"walletId\":\"w-1\"}"
        );

        // ACT : créer la commande (publie sur Kafka)
        service.createOrder(command).block();

        // ASSERT : vérifier qu'un message a été reçu dans les 5 secondes
        ConsumerRecord<String, String> received = receivedMessages.poll(5, TimeUnit.SECONDS);

        assertThat(received).isNotNull();
        assertThat(received.value()).contains("ORDER_CREATED");
        assertThat(received.value()).contains("TOPUP");
    }
}
