package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws java.sql.SQLException {
        DataRetriever dr = new DataRetriever();
        Dish dish = dr.findDishById(2);
        // --- TEST 1 : Doit réussir sur la Table 3 (ORD103 est fini) ---
        try {
            RestaurantTable table3 = new RestaurantTable(3, 3, new ArrayList<>());

            Order order1 = new Order();
            order1.setReference("CMD-" + Instant.now().getEpochSecond());
            order1.setCreationDatetime(Instant.now());

            order1.setDishOrderList(List.of(new DishOrder(null, dish, 1)));

            order1.setTableOrder(new TableOrder(
                    table3,
                    Instant.parse("2026-01-29T12:00:00Z"),
                    Instant.parse("2026-01-29T14:00:00Z")
            ));

            Order saved = dr.saveOrder(order1);
            System.out.println("Commande créée: " + saved.getReference());
        } catch (RuntimeException e) {
            throw new RuntimeException("Test 1 échoué: " + e.getMessage());
        }

        // --- TEST 2 : Doit échouer sur la Table 1 (Conflit avec ORD101 jusqu'à 13:20) ---
        try {
            RestaurantTable table1 = new RestaurantTable(1, 1, new ArrayList<>());

            Order order2 = new Order();
            order2.setReference("CMD-CONFLIT");
            order2.setCreationDatetime(Instant.now());

            order2.setDishOrderList(List.of(new DishOrder(null, dish, 1)));

            order2.setTableOrder(new TableOrder(
                    table1,
                    Instant.parse("2026-01-29T12:30:00Z"),
                    Instant.parse("2026-01-29T13:30:00Z")
            ));

            dr.saveOrder(order2);
            System.out.println("Test 2 devrait échouer mais n'a pas échoué");
        } catch (RuntimeException e) {
            System.out.println("Test 2 réussi (Bloqué par conflit) : " + e.getMessage());
        }
    }
}