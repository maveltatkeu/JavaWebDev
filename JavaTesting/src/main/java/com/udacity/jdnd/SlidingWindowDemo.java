package com.udacity.jdnd;

public class SlidingWindowDemo {

  public static int findMaxSum(int[] arr, int k) {
    int n = arr.length;

    // Cas d'erreur : si le tableau est plus petit que la fenêtre
    if (n < k) {
      System.out.println("Taille invalide");
      return -1;
    }

    // 1. Calculer la somme de la TOUTE PREMIÈRE fenêtre
    int windowSum = 0;
    for (int i = 0; i < k; i++) {
      windowSum += arr[i];
    }

    int maxSum = windowSum;

    // 2. Faire glisser la fenêtre sur le reste du tableau
    // On commence à l'indice k (le premier élément après la 1ère fenêtre)
    for (int i = k; i < n; i++) {
      // On ajoute l'élément entrant (arr[i])
      // et on retire l'élément sortant (arr[i - k])
      windowSum += arr[i] - arr[i - k];

      // On met à jour le maximum si la nouvelle fenêtre est plus grande
      maxSum = Math.max(maxSum, windowSum);
    }

    return maxSum;
  }

  public static int findMaxSum2(int[] arr, int k) {
    if (arr.length < k) {
      System.out.println("Taille inferieure a : " + k);
      return -1;
    }
    int maxSum = 0;

    for (int i = 0; i < k; i++) {
      maxSum += arr[i];
    }
    int m = maxSum;
    for (int j = k; j < arr.length; j++) {
      m += arr[j] - arr[j - k];
      maxSum = Math.max(maxSum, m);
    }
    return maxSum;
  }

  public static void main(String[] args) {
    int[] data = {2, 1, 5, 1, 3, 2, 4, 8, -19, 0, 5};
    int k = 3;
    System.out.println("Somme max de " + k + " éléments : " + findMaxSum2(data, k));
    // Résultat attendu : 9 (5+1+3)
  }
}