package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;

import java.util.List;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
//
//        System.out.println("==== Test a) findDishById(1) ====");
//        try {
//            Dish dish1 = dr.findDishById(1);
//            System.out.println("Dish: " + dish1.getName());
//            System.out.println("Ingredients:");
//            for (Ingredient ing : dish1.getIngredients()) {
//                System.out.println("- " + ing.getName());
//            }
//        } catch (RuntimeException e) {
//            System.out.println("Exception: " + e.getMessage());
//        }
//
//        System.out.println("\n==== Test b) findDishById(999) ====");
//        try {
//            Dish dish999 = dr.findDishById(999);
//            System.out.println("Dish: " + dish999.getName());
//        } catch (RuntimeException e) {
//            System.out.println("Exception levée ✔ : " + e.getMessage());
//        }
//
//        System.out.println("\n==== Test c) findIngredients(page=2, size=2) ====");
//        List<Ingredient> page2Ingredients = dr.findIngredients(2, 2);
//        for (Ingredient ing : page2Ingredients) {
//            System.out.println("- " + ing.getName());
//        }
//
//        System.out.println("\n==== Test d) findIngredients(page=3, size=5) ====");
//        List<Ingredient> page3Ingredients = dr.findIngredients(3, 5);
//        System.out.println(page3Ingredients.isEmpty() ? "Liste vide ✔" : page3Ingredients);
//
//        System.out.println("\n==== Test e) findDishsByIngredientName('eur') ====");
//        List<Dish> dishesWithEur = dr.findDishesByIngredientName("eur");
//        for (Dish d : dishesWithEur) {
//            System.out.println("Dish: " + d.getName());
//        }
//
//        System.out.println("\n==== Test f) findIngredientsByCriteria(category=VEGETABLE) ====");
//        List<Ingredient> vegIngredients = dr.findIngredientByCriteria(
//                null, CategoryEnum.VEGETABLE, null, 1, 10
//        );
//        for (Ingredient ing : vegIngredients) {
//            System.out.println("- " + ing.getName());
//        }
//
//        System.out.println("\n==== Test g) findIngredientsByCriteria(name='cho', dishName='Sal') ====");
//        List<Ingredient> testG = dr.findIngredientByCriteria(
//                "cho", null, "Sal", 1, 10
//        );
//        System.out.println(testG.isEmpty() ? "Liste vide ✔" : testG);
//
//        System.out.println("\n==== Test h) findIngredientsByCriteria(name='cho', dishName='gâteau') ====");
//        List<Ingredient> testH = dr.findIngredientByCriteria(
//                "cho", null, "gâteau", 1, 10
//        );
//        for (Ingredient ing : testH) {
//            System.out.println("- " + ing.getName());
//        }
//
//        System.out.println("\n==== Test i) createIngredients([Fromage, Oignon]) ====");
//        try {
//            List<Ingredient> newIngredients = new ArrayList<>();
//            newIngredients.add(new Ingredient("Fromage", 1200.0, CategoryEnum.DAIRY));
//            newIngredients.add(new Ingredient("Oignon", 500.0, CategoryEnum.VEGETABLE));
//            List<Ingredient> createdIngredients = dr.createIngredients(newIngredients);
//            for (Ingredient ing : createdIngredients) {
//                System.out.println("- " + ing.getName());
//            }
//        } catch (RuntimeException e) {
//            System.out.println("Exception: " + e.getMessage());
//        }
//
//        System.out.println("\n==== Test j) createIngredients doublon [Carotte, Laitue] ====");
//        try {
//            List<Ingredient> dupIngredients = new ArrayList<>();
//            dupIngredients.add(new Ingredient("Carotte", 2000.0, CategoryEnum.VEGETABLE));
//            dupIngredients.add(new Ingredient("Laitue", 2000.0, CategoryEnum.VEGETABLE));
//            dr.createIngredients(dupIngredients);
//        } catch (RuntimeException e) {
//            System.out.println("Exception levée ✔ : " + e.getMessage());
//        }
//
//        System.out.println("\n==== Test k) saveDish(Soupe de légumes) ====");
//        Dish soupe = new Dish();
//        soupe.setName("Soupe de légumes");
//        soupe.setDishType(DishEnum.START);
//        List<Ingredient> soupeIngredients = new ArrayList<>();
//        soupeIngredients.add(new Ingredient("Oignon", 500.0, CategoryEnum.VEGETABLE));
//        soupe.setIngredients(soupeIngredients);
//        Dish savedSoupe = dr.saveDish(soupe);
//        System.out.println("Dish créé: " + savedSoupe.getName());
//        for (Ingredient ing : savedSoupe.getIngredients()) {
//            System.out.println("- " + ing.getName());
//        }
//
//        System.out.println("\n==== Test l) saveDish(update Salade fraîche) ====");
//        Dish salade = dr.findDishById(1);
//        List<Ingredient> saladeIngredients = new ArrayList<>(salade.getIngredients());
//        saladeIngredients.add(new Ingredient("Oignon", 500.0, CategoryEnum.VEGETABLE));
//        saladeIngredients.add(new Ingredient("Fromage", 1200.0, CategoryEnum.DAIRY));
//        salade.setIngredients(saladeIngredients);
//        salade = dr.saveDish(salade);
//        System.out.println("Dish mis à jour: " + salade.getName());
//        for (Ingredient ing : salade.getIngredients()) {
//            System.out.println("- " + ing.getName());
//        }
//
//        System.out.println("\n==== Test m) saveDish(update Salade de fromage) ====");
//        salade.setName("Salade de fromage");
//        List<Ingredient> fromageOnly = new ArrayList<>();
//        fromageOnly.add(new Ingredient("Fromage", 1200.0, CategoryEnum.DAIRY));
//        salade.setIngredients(fromageOnly);
//        salade = dr.saveDish(salade);
//        System.out.println("Dish mis à jour: " + salade.getName());
//        for (Ingredient ing : salade.getIngredients()) {
//            System.out.println("- " + ing.getName());
//        }
//    }
//
        DataRetriever dr = new DataRetriever();

        Dish salade = dr.findDishById(1);
        System.out.println("Marge salade = " + salade.getGrossMargin());

        Dish gateau = dr.findDishById(4);
        System.out.println(gateau.getGrossMargin());

        Dish riz = dr.findDishById(3);
        riz.setPrice(4000.0);
        riz = dr.saveDish(riz);

        System.out.println("Marge riz = " + riz.getGrossMargin());

    }
}
