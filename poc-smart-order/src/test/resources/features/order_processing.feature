# language: en
# Feature file Cucumber BDD - lisible par les Product Owners et QA

Feature: Order Processing Orchestration
  As a platform, I want to process customer orders asynchronously
  So that the system remains resilient and scalable

  Background:
    Given the event publisher is available

  # ============================
  # Scenario: Commande physique
  # ============================
  Scenario: Successful physical order creation
    Given a valid physical order with amount 9999 cents
    When the order is submitted
    Then the order should be created with status PENDING
    And the order ID should be generated
    And an ORDER_CREATED event should be published
    And it should be a PHYSICAL order

  # ============================
  # Scenario: Commande digitale
  # ============================
  Scenario: Successful digital order creation
    Given a valid digital order
    When the order is submitted
    Then the order should be created with status PENDING
    And an ORDER_CREATED event should be published
    And it should be a DIGITAL order

  # ============================
  # Scenario: Recharge (TopUp)
  # ============================
  Scenario: Successful topup order creation
    Given a valid topup order
    When the order is submitted
    Then the order should be created with status PENDING
    And an ORDER_CREATED event should be published
    And it should be a TOPUP order

  # ============================
  # Scenario: Echec de paiement
  # ============================
  Scenario: Order fails when payment service is unavailable
    Given a valid physical order with amount 5000 cents
    And the payment service is unavailable
    When the order is submitted
    Then the order should fail with an error
