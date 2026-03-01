package com.poc.smartorder.application.port.out;

import com.poc.smartorder.domain.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public interface PaymentPort {
    // Port pour le débit du paiement [cite: 81, 82]
    Mono<Void> charge(Order order);
}


