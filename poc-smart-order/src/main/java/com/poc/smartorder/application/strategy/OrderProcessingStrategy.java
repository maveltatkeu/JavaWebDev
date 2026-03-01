package com.poc.smartorder.application.strategy;

import com.poc.smartorder.domain.Order;
import com.poc.smartorder.domain.OrderType;
import reactor.core.publisher.Mono;

public interface OrderProcessingStrategy {
  Mono<Void> process(Order order);

  OrderType getType(); // Pour identifier la stratégie
}