package com.company.order.application.factory;

import com.company.order.application.strategy.OrderProcessingStrategy;
import com.company.order.domain.model.OrderType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PATTERN FACTORY – Sélection dynamique de la stratégie de traitement.
 *
 * Utilise l'auto-injection de Spring : toutes les beans implémentant
 * OrderProcessingStrategy sont injectées automatiquement dans la liste.
 *
 * Avantage : ajouter une nouvelle stratégie ne nécessite aucune modification
 * de cette classe (Open/Closed Principle respecté).
 *
 * La Map est construite à l'initialisation (une seule fois) pour des
 * lookups O(1) lors du traitement des commandes.
 */
@Component
public class OrderStrategyFactory {

    /** Map type → stratégie, construite au démarrage */
    private final Map<OrderType, OrderProcessingStrategy> strategies;

    /**
     * Spring injecte automatiquement toutes les implémentations de OrderProcessingStrategy.
     * @param strategyList liste de toutes les stratégies enregistrées comme @Component
     */
    public OrderStrategyFactory(List<OrderProcessingStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        OrderProcessingStrategy::supportedType,
                        Function.identity()
                ));
    }

    /**
     * Retourne la stratégie appropriée pour le type de commande.
     *
     * @param type type de commande
     * @return stratégie correspondante
     * @throws IllegalArgumentException si aucune stratégie n'est enregistrée pour ce type
     */
    public OrderProcessingStrategy getStrategy(OrderType type) {
        OrderProcessingStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy registered for order type: " + type +
                    ". Available types: " + strategies.keySet()
            );
        }
        return strategy;
    }
}
