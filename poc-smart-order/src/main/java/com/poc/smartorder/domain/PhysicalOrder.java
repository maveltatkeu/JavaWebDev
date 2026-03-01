package com.poc.smartorder.domain;

import java.util.UUID;

public record PhysicalOrder(UUID id) implements Order {

    @Override
    public OrderType type() {
        return OrderType.PHYSICAL;
    }
}



