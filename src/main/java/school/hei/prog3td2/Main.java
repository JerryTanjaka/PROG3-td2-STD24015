package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. Initialisation
        DataRetriever dr = new DataRetriever();
        UnitConverter converter = new UnitConverter();

        System.out.println("======================================================");
        System.out.println("   TEST DU SCÉNARIO BONUS : CONVERSION DES UNITÉS    ");
        System.out.println("======================================================");

        try {
            // Liste des tests basés sur le tableau du PDF (Page 2)
            // Format : ID_DB, Nom, Quantité_Sortie, Unité_Sortie, Stock_Attendu
            runTest(dr, converter, 1, "Laitue", 2.0, UnitType.PCS, 4.0);
            runTest(dr, converter, 2, "Tomate", 5.0, UnitType.PCS, 3.5);
            runTest(dr, converter, 3, "Poulet", 4.0, UnitType.PCS, 9.5);
            runTest(dr, converter, 4, "Chocolat", 1.0, UnitType.L, 2.6);
            runTest(dr, converter, 5, "Beurre", 1.0, UnitType.L, 2.3);

        } catch (Exception e) {
            System.err.println("Erreur durant les tests : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n======================================================");
        System.out.println("      TEST D'UNE COMMANDE (SAVEORDER) AVEC CONV      ");
        System.out.println("======================================================");

        try {
            // On simule une commande pour tester l'intégration dans DataRetriever
            Order order = new Order();
            order.setReference("ORD-B-01");
            order.setCreationDatetime(Instant.now());

            // On commande un plat qui existe (ex: Salade fraîche ID 1)
            Dish salade = dr.findDishById(1);
            List<DishOrder> items = new ArrayList<>();
            items.add(new DishOrder(null, salade, 1));
            order.setDishOrders(items);

            // saveOrder va utiliser UnitConverter pour voir si le stock suffit
            dr.saveOrder(order);
            System.out.println("Résultat : Commande ORD-B-01 validée (Stock suffisant après conversion).");

        } catch (RuntimeException e) {
            System.out.println("Résultat attendu : " + e.getMessage());
        }
    }

    /**
     * Exécute la simulation du PDF pour un ingrédient donné
     */
    private static void runTest(DataRetriever dr, UnitConverter converter,
                                int id, String name, double qtyOut, UnitType unitOut, double expected) {

        // Récupération de l'ingrédient et ses mouvements initiaux (Stock initial)
        Ingredient ing = dr.findIngredientById(id);
        double stockInitial = ing.getStockValueAt(Instant.now(), converter).getQuantity();

        // Ajout du mouvement de sortie (OUT) décrit dans le tableau du PDF
        StockMovement movement = new StockMovement(
                null,
                new StockValue(qtyOut, unitOut),
                MovementTypeEnum.OUT,
                Instant.now()
        );
        ing.getStockMovementList().add(movement);

        // Calcul du stock final
        double stockFinal = ing.getStockValueAt(Instant.now(), converter).getQuantity();

        // Calcul de la valeur de sortie en KG (pour le détail)
        double outInKg = converter.convert(ing.getName(), qtyOut, unitOut, UnitType.KG);

        // Affichage des résultats
        System.out.printf("Ingrédient: %-10s | Stock Avant: %.1f KG | Sortie: %.1f %-3s (soit %.1f KG) | Final: %.1f KG | Status: %s\n",
                name,
                stockInitial,
                qtyOut,
                unitOut,
                outInKg,
                stockFinal,
                (Math.abs(stockFinal - expected) < 0.01 ? " OK" : " ERREUR (Attendu: " + expected + ")")
        );
    }
}