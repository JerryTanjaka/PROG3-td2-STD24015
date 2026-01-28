package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 1. Initialisation des outils
        DataRetriever dr = new DataRetriever();
        UnitConverter converter = new UnitConverter(); // Instance (pas de static)

        System.out.println("========== TEST DES CONVERSIONS (BONUS PDF) ==========");

        // 2. Récupération des ingrédients depuis la DB (Laitue=1, Tomate=2, Poulet=3, Chocolat=4, Beurre=5)
        // On suppose que le stock initial en DB (IN) est celui du PDF (5.0, 4.0, 10.0, 3.0, 2.5)

        try {
            // Test spécifique pour les ingrédients du PDF
            checkIngredientStock(dr, converter, 1, "Laitue", 2.0, UnitType.PCS);   // 2 PCS = 1.0 KG
            checkIngredientStock(dr, converter, 2, "Tomate", 5.0, UnitType.PCS);   // 5 PCS = 0.5 KG
            checkIngredientStock(dr, converter, 3, "Poulet", 4.0, UnitType.PCS);   // 4 PCS = 0.5 KG
            checkIngredientStock(dr, converter, 4, "Chocolat", 1.0, UnitType.L);   // 1 L = 0.4 KG
            checkIngredientStock(dr, converter, 5, "Beurre", 1.0, UnitType.L);     // 1 L = 0.2 KG

        } catch (Exception e) {
            System.err.println("Erreur durant le test : " + e.getMessage());
        }

        System.out.println("\n========== TEST SAUVEGARDE COMMANDE (SAVEORDER) ==========");

        // 3. Test de création d'une commande
        try {
            Order newOrder = new Order();
            newOrder.setReference("ORD00001");
            newOrder.setCreationDatetime(Instant.now());

            // On commande une Salade fraîche (ID 1) qui utilise de la Laitue et Tomate
            Dish salade = dr.findDishById(1);
            DishOrder item1 = new DishOrder(null, salade, 2); // On en veut 2 !

            List<DishOrder> items = new ArrayList<>();
            items.add(item1);
            newOrder.setDishOrders(items);

            // saveOrder va utiliser le UnitConverter en interne pour vérifier les stocks
            dr.saveOrder(newOrder);
            System.out.println("Commande enregistrée avec succès ! Stocks vérifiés avec conversion.");

        } catch (RuntimeException e) {
            System.out.println("Echec de la commande (normal si stock bas) : " + e.getMessage());
        }
    }

    /**
     * Méthode utilitaire pour vérifier le stock selon le PDF
     */
    private static void checkIngredientStock(DataRetriever dr, UnitConverter converter,
                                             int id, String label, double qtyOut, UnitType unitOut) {

        Ingredient ing = dr.findIngredientById(id);
        double stockAvant = ing.getStockValueAt(Instant.now(), converter.getQuantity());
é
        // On simule manuellement la sortie du PDF (sans l'écrire en DB pour le test)
        StockMovement sortie = new StockMovement(
                null,
                new StockValue(qtyOut, unitOut),
                MovementTypeEnum.OUT,
                Instant.now()
        );
        ing.getStockMovementList().add(sortie);

        double sortieEnKg = converter.convert(ing.getName(), qtyOut, unitOut, UnitType.KG);
        double stockFinal = ing.getStockValueAt(Instant.now(), converter.getQuantity());

        System.out.printf("[%s] Avant: %.1f KG | Sortie: %.1f %s (soit %.2f KG) | Final: %.2f KG\n",
                label, stockAvant, qtyOut, unitOut, sortieEnKg, stockFinal);
    }
}