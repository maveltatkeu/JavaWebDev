package com.company.order.application.usecase;

import com.company.order.domain.model.*;
import org.springframework.stereotype.Component;

/**
 * Mapper de la couche application.
 *
 * Convertit un CreateOrderCommand (objet d'entrée) en entité Order (domaine).
 * Isole la logique de mapping du use case.
 *
 * Le switch sur OrderType avec pattern exhaustif garantit que
 * tout nouveau type sera détecté à la compilation (Java 17 sealed classes).
 */
@Component
public class OrderMapper {

    /**
     * Crée l'entité Order appropriée selon le type de commande.
     *
     * @param orderId       UUID généré
     * @param correlationId ID de corrélation pour la traçabilité
     * @param command       Commande d'entrée
     * @return Order (PhysicalOrder, DigitalOrder ou TopUpOrder)
     */
    public Order toOrder(String orderId, String correlationId, CreateOrderCommand command) {
        return switch (command.orderType()) {
            case PHYSICAL -> new PhysicalOrder(
                    orderId,
                    command.customerId(),
                    command.amountInCents(),
                    correlationId,
                    command.deliveryAddress(),
                    command.productSku(),
                    command.quantity() != null ? command.quantity() : 1
            );
            case DIGITAL -> new DigitalOrder(
                    orderId,
                    command.customerId(),
                    command.amountInCents(),
                    correlationId,
                    command.licenseKey(),
                    command.productCode(),
                    command.activationEmail()
            );
            case TOPUP -> new TopUpOrder(
                    orderId,
                    command.customerId(),
                    command.amountInCents(),
                    correlationId,
                    command.accountNumber(),
                    command.provider()
            );
        };
    }
}
