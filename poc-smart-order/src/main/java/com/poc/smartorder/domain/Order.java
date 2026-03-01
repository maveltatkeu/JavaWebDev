package com.poc.smartorder.domain;

import java.util.UUID;

public sealed interface Order permits PhysicalOrder, DigitalOrder, TopUpOrder {
    UUID id();
    OrderType type();
}