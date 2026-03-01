package com.poc.smartorder.application.strategy.impl;

import com.poc.smartorder.application.port.out.EventPublisherPort;
import com.poc.smartorder.application.port.out.InventoryPort;
import com.poc.smartorder.application.port.out.PaymentPort;
import com.poc.smartorder.application.strategy.OrderProcessingStrategy;
import com.poc.smartorder.domain.Order;
import com.poc.smartorder.domain.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PhysicalOrderStrategy implements OrderProcessingStrategy {
  private final PaymentPort paymentPort;
  private final InventoryPort inventoryPort;
  private final EventPublisherPort eventPublisher;

  @Override
  public Mono<Void> process(Order order) {
    return paymentPort.charge(order)
        .then(inventoryPort.reserve(order))
        .then(eventPublisher.publish("order.completed"));
  }

  @Override
  public OrderType getType() {
    return OrderType.PHYSICAL;
  }
}