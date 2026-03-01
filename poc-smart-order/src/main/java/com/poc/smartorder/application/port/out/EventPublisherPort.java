package com.poc.smartorder.application.port.out;

import reactor.core.publisher.Mono;

public interface EventPublisherPort {
    // Port pour notifier les autres microservices [cite: 87, 88]
    Mono<Void> publish(Object event);
}