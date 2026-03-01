package com.poc.smartorder.domain;

import java.util.UUID;

public record TopUpOrder(UUID id) implements Order {
    @Override public OrderType type() { return OrderType.TOPUP; }
}
