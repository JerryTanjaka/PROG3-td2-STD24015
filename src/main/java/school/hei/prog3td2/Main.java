package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;
import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();
        UnitConverter converter = new UnitConverter();

        System.out.println("========== VÉRIFICATION DU SCÉNARIO PDF (PAGE 2) ==========");

        // On lance les tests sur les 5 ingrédients du tableau
        runBonusTest(dr, converter, 1, "Laitue", 2.0, UnitType.PCS, 4.0);
        runBonusTest(dr, converter, 2, "Tomate", 5.0, UnitType.PCS, 3.5);
        runBonusTest(dr, converter, 3, "Poulet", 4.0, UnitType.PCS, 9.5);
        runBonusTest(dr, converter, 4, "Chocolat", 1.0, UnitType.L, 2.6);
        runBonusTest(dr, converter, 5, "Beurre", 1.0, UnitType.L, 2.3);

        System.out.println("\n========== TEST SAUVEGARDE COMMANDE ==========");
        try {
            Order order = new Order();
            order.setReference("ORD" + System.currentTimeMillis() % 100000);
            order.setCreationDatetime(Instant.now());

            Dish salade = dr.findDishById(1);
            ArrayList<DishOrder> items = new ArrayList<>();
            items.add(new DishOrder(null, salade, 1));
            order.setDishOrders(items);

            dr.saveOrder(order);
            System.out.println("Commande enregistrée ! Montant HT: " + order.getTotalAmountWithoutVAT());
        } catch (Exception e) {
            System.out.println("Erreur (potentiellement stock bas) : " + e.getMessage());
        }
    }

    private static void runBonusTest(DataRetriever dr, UnitConverter converter, int id, String name, double qty, UnitType unit, double expected) {
        try {
            Ingredient ing = dr.findIngredientById(id);
            double avant = ing.getStockValueAt(Instant.now(), converter).getQuantity();

            // Simulation de la sortie du tableau
            StockMovement out = new StockMovement(null, new StockValue(qty, unit), MovementTypeEnum.OUT, Instant.now());
            ing.getStockMovementList().add(out);

            double apres = ing.getStockValueAt(Instant.now(), converter).getQuantity();

            System.out.printf("[%s] Sortie: %.1f %s | Stock: %.2f KG | Status: %s\n",
                    name, qty, unit, apres, (Math.abs(apres - expected) < 0.01 ? " OK" : " ERREUR (Attendu: " + expected + ")"));
        } catch (Exception e) {
            System.out.println("Erreur sur " + name + ": " + e.getMessage());
        }
    }
}