package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;
import java.time.Instant;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        // 1. TEST MARGE (TD PRÉCÉDENTS)
        try {
            Dish poulet = dr.findDishById(2);
            System.out.println("--- TEST 1 : MARGE ---");
            System.out.println("Poulet -> Coût: " + poulet.getDishCost() + " | Marge: " + poulet.getGrossMargin());
        } catch (Exception e) { System.out.println("Erreur Marge: " + e.getMessage()); }

        // 2. TEST CONFLIT TABLE 1 (ORD101 finit à 13:20, on tente à 12:00)
        System.out.println("\n--- TEST 2 : CONFLIT TABLE 1 ---");
        try {
            save(dr, "FAIL-T1", 1, "2026-01-29T12:00:00Z", "2026-01-29T13:00:00Z", 1);
        } catch (Exception e) {
            System.out.println("Résultat (Attendu): " + e.getMessage());
        }

        // 3. TEST SUCCÈS TABLE 2 (Libre depuis 10:15)
        System.out.println("\n--- TEST 3 : SUCCÈS TABLE 2 ---");
        try {
            save(dr, "OK-T2", 2, "2026-01-29T12:00:00Z", "2026-01-29T13:00:00Z", 1);
            System.out.println("Résultat: Commande enregistrée avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
        }

        // 4. TEST STOCK INSUFFISANT
        System.out.println("\n--- TEST 4 : STOCK ---");
        try {
            save(dr, "FAIL-STOCK", 3, "2026-01-29T15:00:00Z", "2026-01-29T16:00:00Z", 9999);
        } catch (Exception e) {
            System.out.println("Résultat (Attendu): " + e.getMessage());
        }
    }

    // Helper pour créer et sauvegarder une commande (POO Bonus)
    private static void save(DataRetriever dr, String ref, int tableId, String arr, String dep, int qty) throws Exception {
        Dish dish = dr.findDishById(2); // On utilise le Poulet (ID 2)
        Order o = new Order();
        o.setReference(ref);
        o.setCreationDatetime(Instant.now());
        o.setDishOrderList(List.of(new DishOrder(null, dish, qty)));

        // Structure TableOrder pour le Bonus
        RestaurantTable table = new RestaurantTable(tableId, tableId, null);
        o.setTableOrder(new TableOrder(table, Instant.parse(arr), Instant.parse(dep)));

        dr.saveOrder(o);
    }
}