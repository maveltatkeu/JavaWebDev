# Smart Order Orchestrator – POC

Microservice d'orchestration de commandes avec **Architecture Hexagonale**, **Kafka event-driven** et **Spring WebFlux**.

---

## Stack Technique

| Composant | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.2 |
| Spring WebFlux | intégré |
| Spring Kafka | 3.x |
| Cucumber | 7.x |
| Mockito | 5.x |
| Testcontainers / EmbeddedKafka | latest |

---

## Architecture du Projet

```
com.company.order
├── domain/                          ← CŒUR MÉTIER (0 dépendance Spring)
│   ├── model/
│   │   ├── Order.java               ← sealed interface (Java 17)
│   │   ├── PhysicalOrder.java       ← record
│   │   ├── DigitalOrder.java        ← record
│   │   ├── TopUpOrder.java          ← record
│   │   ├── OrderEvent.java          ← event publié sur Kafka
│   │   ├── OrderType.java
│   │   ├── OrderStatus.java
│   │   └── PaymentResult.java
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateOrderUseCase.java     ← Inbound Port
│   │   │   └── CreateOrderCommand.java
│   │   └── out/
│   │       ├── PaymentPort.java            ← Outbound Ports
│   │       ├── InventoryPort.java
│   │       ├── EventPublisherPort.java
│   │       └── BillingPort.java
│   └── exception/
│       ├── OrderProcessingException.java   ← base (retryable flag)
│       ├── PaymentDeclinedException.java   ← non-retryable → DLQ
│       └── InsufficientStockException.java ← non-retryable → DLQ
│
├── application/                     ← ORCHESTRATION (use cases, patterns)
│   ├── usecase/
│   │   └── CreateOrderService.java  ← Implémentation du UseCase
│   ├── strategy/
│   │   ├── OrderProcessingStrategy.java    ← Interface Strategy
│   │   ├── PhysicalOrderStrategy.java      ← payment + inventory + event
│   │   ├── DigitalOrderStrategy.java       ← payment + billing + event
│   │   └── TopUpOrderStrategy.java         ← payment + event
│   ├── factory/
│   │   └── OrderStrategyFactory.java       ← Sélection dynamique
│   └── visitor/
│       ├── OrderVisitor.java               ← Interface Visitor
│       └── OrderValidationVisitor.java     ← Règles de validation
│
├── infrastructure/                  ← ADAPTATEURS (Spring, Kafka, HTTP)
│   ├── kafka/
│   │   ├── config/
│   │   │   └── KafkaTopicsConfig.java      ← Déclaration des topics
│   │   ├── producer/
│   │   │   └── KafkaEventPublisher.java    ← impl EventPublisherPort
│   │   └── consumer/
│   │       └── OrderCreatedConsumer.java   ← @RetryableTopic + @DltHandler
│   ├── web/
│   │   ├── controller/
│   │   │   └── OrderController.java        ← WebFlux REST Controller
│   │   └── dto/
│   │       ├── CreateOrderRequest.java
│   │       └── CreateOrderResponse.java
│   └── external/
│       ├── payment/
│       │   └── StripePaymentAdapter.java   ← impl PaymentPort
│       ├── inventory/
│       │   └── InventoryApiAdapter.java    ← impl InventoryPort
│       └── billing/
│           └── BillingApiAdapter.java      ← impl BillingPort
│
└── config/
    └── WebClientConfig.java                ← WebClient beans

src/test/
├── domain/
│   └── PhysicalOrderTest.java              ← Tests modèle (0 Spring)
├── application/
│   ├── OrderStrategyFactoryTest.java        ← Tests Factory
│   ├── PhysicalOrderStrategyTest.java       ← Tests Strategy (Mockito + StepVerifier)
│   ├── CreateOrderServiceTest.java          ← Tests UseCase
│   └── OrderValidationVisitorTest.java      ← Tests Visitor
├── infrastructure/
│   ├── OrderControllerTest.java             ← Tests WebFlux (@WebFluxTest)
│   └── KafkaIntegrationTest.java            ← Tests Kafka (EmbeddedKafka)
└── bdd/
    ├── CucumberRunner.java                  ← Runner JUnit 5
    ├── config/
    │   └── CucumberSpringConfig.java
    └── steps/
        └── OrderProcessingSteps.java        ← Step Definitions

src/test/resources/features/
└── order_processing.feature                 ← Scenarios Gherkin
```

---

## Flow Kafka – Cas nominal

```
POST /orders
    │
    ▼
OrderController (WebFlux)
    │ createOrder(command)
    ▼
CreateOrderService
    │ publish(ORDER_CREATED)
    ▼
KafkaEventPublisher ──► Topic: order.created
                              │
                    ◄─────────┘
OrderCreatedConsumer
    │ selectStrategy(type)
    ▼
OrderProcessingStrategy
    │ charge()
    ▼
PaymentAdapter
    │ OK
    ▼
InventoryAdapter (PHYSICAL uniquement)
    │ OK
    ▼
publish(ORDER_COMPLETED) ──► Topic: order.completed
```

## Retry Exponentiel

```
Tentative 1 → immédiat
Tentative 2 → 1s  (topic: order.created-retry-1000)
Tentative 3 → 2s  (topic: order.created-retry-2000)
Tentative 4 → 4s  (topic: order.created-retry-4000)
Échec définitif   → DLQ (topic: order.created.DLT)
```

---

## Lancement

```bash
# Démarrer Kafka (Docker)
docker run -d --name kafka \
  -p 9092:9092 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  confluentinc/cp-kafka:latest

# Lancer l'application
./mvnw spring-boot:run

# Tester l'API
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: test-001" \
  -d '{
    "customerId": "customer-001",
    "orderType": "PHYSICAL",
    "amountInCents": 9999,
    "metadata": "{\"productSku\":\"SKU-001\",\"shippingAddress\":\"Paris\"}"
  }'
```

## Tests

```bash
# Tous les tests
./mvnw test

# Tests unitaires uniquement
./mvnw test -pl . -Dtest="*Test"

# Tests BDD Cucumber uniquement
./mvnw test -Dtest="CucumberRunner"

# Tests d'intégration Kafka
./mvnw test -Dtest="KafkaIntegrationTest"
```

---

## Concepts Clés

### Pourquoi les Sealed Classes (Java 17) ?
Les `sealed interface Order` force le compilateur à garantir l'exhaustivité lors des `switch` :
si on ajoute `SubscriptionOrder`, le code ne compile plus tant qu'on ne gère pas ce nouveau cas.

### Pourquoi Strategy + Factory ?
Le consumer Kafka reçoit un message sans savoir son type au moment de la compilation.
La Factory permet une sélection **dynamique** en O(1) sans `if/else` à rallonge.

### Pourquoi Kafka Retry plutôt qu'un retry applicatif ?
- **Pas de thread bloqué** : le retry se fait via des topics intermédiaires
- **Backpressure naturelle** : Kafka gère la charge automatiquement
- **Observabilité** : on peut monitorer les topics retry-N

### Pourquoi la DLQ est critique en production ?
Sans DLQ, les messages en échec bloquent le consumer (partition lag infini).
La DLQ permet l'analyse post-mortem et la reprise manuelle.
