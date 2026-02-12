package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.Dish;
import school.hei.prog3td2.model.Ingredient;
import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.StockValue;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
        int idLaitue = 1;
        int idPouletGrille = 2;
        Instant maintenant = Instant.now();

        System.out.println("=== QUESTION 1 : COMPARAISON STOCK (Objet vs SQL) ===");

        //  Approche Orientée Objet (Ancienne)
//        Ingredient ing = dataRetriever.findIngredientById(idLaitue);
//        StockValue stockJava = ing.getStockValueAt(maintenant);
//        System.out.println("Stock (Approche Objet) : " + stockJava.getQuantity() + " " + stockJava.getUnit());

        //  Approche Push-down (Nouvelle via SQL)
//        StockValue stockSql = dataRetriever.getStockValueAt(maintenant, idLaitue);
//        System.out.println("Stock (Approche SQL)   : " + stockSql.getQuantity() + " " + stockSql.getUnit());
//        System.out.println("Résultats identiques ? " + (stockJava.getQuantity().equals(stockSql.getQuantity())));


        // 2 TEST DU COÛT DU PLAT (Question 2a) ---
        System.out.println("\n--- 2. COÛT DU PLAT (Poulet Grillé) ---");
        // Version POO
        Dish plat = dataRetriever.findDishById(idPouletGrille);
        Double coutPOO = plat.getDishCost();

        // Version Push-down
        Double coutSQL = dataRetriever.getDishCostSQL(idPouletGrille);

        System.out.println("[POO] Coût : " + coutPOO);
        System.out.println("[SQL] Coût : " + coutSQL);
        System.out.println("Identiques ? " + (coutPOO.equals(coutSQL) ? "OUI " : "NON "));


        // --- 3. TEST DE LA MARGE BRUTE (Question 2b) ---
        System.out.println("\n--- 3. MARGE BRUTE (Poulet Grillé) ---");
        // Version POO
        Double margePOO = plat.getGrossMargin();
        // Version Push-down
        Double margeSQL = dataRetriever.getGrossMarginSQL(idPouletGrille);

        System.out.println("[POO] Marge : " + margePOO);
        System.out.println("[SQL] Marge : " + margeSQL);
        System.out.println("Identiques ? " + (margePOO.equals(margeSQL) ? "OUI " : "NON "));

        System.out.println("\n");
    }

}
