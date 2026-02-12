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

        System.out.println("\n=== QUESTION 2 : COÛT ET MARGE (Push-down SQL) ===");

        // Test du Coût (2a)
        Double coutSql = dataRetriever.getDishCostSQL(idPouletGrille);
        System.out.println("Coût du Poulet grillé (via SQL) : " + coutSql);

        // Test de la Marge (2b)
//        Double margeSql = dataRetriever.getGrossMarginSQL(idPouletGrille);
        System.out.println("Marge du Poulet grillé (via SQL) : " + margeSql);
    }
}