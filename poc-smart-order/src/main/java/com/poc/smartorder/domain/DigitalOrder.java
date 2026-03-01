package com.poc.smartorder.domain;

import java.util.UUID;

public record DigitalOrder(UUID id) implements Order {

    @Override
    public OrderType type() {
        return OrderType.DIGITAL;
    }
}
