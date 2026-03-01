package com.company.order.domain.model;

/**
 * Résultat retourné par l'adaptateur de paiement.
 *
 * @param transactionId identifiant de la transaction externe
 * @param success       true si le paiement a été accepté
 * @param errorMessage  message d'erreur en cas d'échec (null si succès)
 */
public record PaymentResult(
        String transactionId,
        boolean success,
        String errorMessage
) {}
