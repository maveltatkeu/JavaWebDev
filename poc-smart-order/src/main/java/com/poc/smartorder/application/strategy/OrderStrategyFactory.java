package com.poc.smartorder.application.strategy;

import com.poc.smartorder.domain.OrderType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OrderStrategyFactory {
  private final Map<OrderType, OrderProcessingStrategy> strategies;

  public OrderStrategyFactory(List<OrderProcessingStrategy> strategyList) {
    // Injection automatique de toutes les implémentations de stratégies [cite: 180]
    this.strategies = strategyList.stream()
        .collect(Collectors.toMap(OrderProcessingStrategy::getType, Function.identity()));
  }

  public OrderProcessingStrategy getStrategy(OrderType type) {
    return strategies.get(type);
  }
}