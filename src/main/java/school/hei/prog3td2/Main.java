package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();

        System.out.println("=== TEST 1 : COÛTS ET MARGES (LEGACY) ===");
        try {
            Dish poulet = dataRetriever.findDishById(2);
            System.out.println("Plat : " + poulet.getName());
            System.out.println("Coût : " + poulet.getDishCost() + " | Marge : " + poulet.getGrossMargin());
        } catch (Exception e) {
            System.err.println("Erreur : " + e.getMessage());
        }

        System.out.println("\n=== TEST 2 : SAUVEGARDE COMMANDE RÉUSSIE (TABLE LIBRE) ===");
        try {
            // 1. Préparation de la table et de l'occupation (POO Bonus)
            RestaurantTable table1 = new RestaurantTable(1, 1, new ArrayList<>());
            TableOrder occupation1 = new TableOrder(
                    table1,
                    Instant.parse("2026-01-29T12:00:00Z"), // Arrivée 12h
                    Instant.parse("2026-01-29T14:00:00Z")  // Départ 14h
            );

            // 2. Préparation de la commande
            Order order1 = new Order();
            order1.setCreationDatetime(Instant.now());
            order1.setTableOrder(occupation1);

            // 3. Ajout d'un plat (Poulet Grillé ID 2)
            Dish poulet = dataRetriever.findDishById(2);
            DishOrder dishOrder = new DishOrder(null, poulet, 1);
            order1.setDishOrderList(List.of(dishOrder));

            // 4. Enregistrement
            dataRetriever.saveOrder(order1);
            System.out.println("Succès : Commande ORD-2026-001 enregistrée sur la Table 1.");
        } catch (Exception e) {
            System.err.println("Erreur inattendue : " + e.getMessage());
        }

        System.out.println("\n=== TEST 3 : CONFLIT DE TABLE (EXCEPTION INFORMÉE) ===");
        try {
            // On tente de réserver la TABLE 1 à 13h (elle est occupée jusqu'à 14h)
            RestaurantTable table1 = new RestaurantTable(1, 1, new ArrayList<>());
            TableOrder occupationConflit = new TableOrder(
                    table1,
                    Instant.parse("2026-01-29T13:00:00Z"), // Conflit ici
                    Instant.parse("2026-01-29T15:00:00Z")
            );

            Order orderConflit = new Order();
            orderConflit.setReference("ORD-CONFLIT");
            orderConflit.setCreationDatetime(Instant.now());
            orderConflit.setTableOrder(occupationConflit);

            Dish poulet = dataRetriever.findDishById(2);
            orderConflit.setDishOrderList(List.of(new DishOrder(null, poulet, 1)));

            System.out.println("Tentative de réservation Table 1 à 13h...");
            dataRetriever.saveOrder(orderConflit);
        } catch (Exception e) {
            // Doit afficher : "La table spécifiée est occupée. Tables libres : [2, 3]"
            System.out.println("Message d'erreur reçu : " + e.getMessage());
        }

        System.out.println("\n=== TEST 4 : VÉRIFICATION DES STOCKS (INSUFFISANT) ===");
        try {
            // On tente de commander 1000 poulets sur la Table 2
            RestaurantTable table2 = new RestaurantTable(2, 2, new ArrayList<>());
            TableOrder occupationTable2 = new TableOrder(
                    table2,
                    Instant.parse("2026-01-29T18:00:00Z"),
                    Instant.parse("2026-01-29T20:00:00Z")
            );

            Order orderTropGros = new Order();
            orderTropGros.setReference("ORD-STOCK-ERROR");
            orderTropGros.setCreationDatetime(Instant.now());
            orderTropGros.setTableOrder(occupationTable2);

            Dish poulet = dataRetriever.findDishById(2);
            // Quantité énorme pour vider le stock
            DishOrder tropDePoulet = new DishOrder(null, poulet, 1000);
            orderTropGros.setDishOrderList(List.of(tropDePoulet));

            dataRetriever.saveOrder(orderTropGros);
        } catch (Exception e) {
            System.out.println("Résultat attendu pour le stock : " + e.getMessage());
        }
    }
}