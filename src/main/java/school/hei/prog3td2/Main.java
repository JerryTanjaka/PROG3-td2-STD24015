package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        DataRetriever dao = new DataRetriever();

        // a) findDishById(1)
        System.out.println("a) findDishById(1)");
        Dish dish1 = dao.findDishById(1);
        System.out.println(dish1.getName());
        dish1.getIngredients().forEach(i -> System.out.println(" - " + i.getName()));
        System.out.println();

        // b) findDishById(999) → exception
        System.out.println("b) findDishById(999)");
        try {
            dao.findDishById(999);
        } catch (RuntimeException e) {
            System.out.println("Exception levée ✔");
        }
        System.out.println();

        // c) findIngredients(page=2, size=2)
        System.out.println("c) findIngredients(page=2, size=2)");
        dao.findIngredients(2, 2)
                .forEach(i -> System.out.println(i.getName()));
        System.out.println();

        // d) findIngredients(page=3, size=5)
        System.out.println("d) findIngredients(page=3, size=5)");
        List<Ingredient> page3 = dao.findIngredients(3, 5);
        System.out.println("Liste vide ? " + page3.isEmpty());
        System.out.println();

        // e) findDishsByIngredientName(\"eur\")
        System.out.println("e) findDishsByIngredientName(\"eur\")");
        dao.findDishesByIngredientName("eur")
                .forEach(d -> System.out.println(d.getName()));
        System.out.println();

        // f) category=VEGETABLE
        System.out.println("f) category=VEGETABLE");
        dao.findIngredientByCriteria(
                null,
                CategoryEnum.VEGETABLE,
                null,
                1,
                10
        ).forEach(i -> System.out.println(i.getName()));
        System.out.println();

        // g) ingredientName=\"cho\", dishName=\"Sal\"
        System.out.println("g) ingredientName=\"cho\", dishName=\"Sal\"");
        List<Ingredient> gResult = dao.findIngredientByCriteria(
                "cho",
                null,
                "Sal",
                1,
                10
        );
        System.out.println("Liste vide ? " + gResult.isEmpty());
        System.out.println();

        // h) ingredientName=\"cho\", dishName=\"gâteau\"
        System.out.println("h) ingredientName=\"cho\", dishName=\"gâteau\"");
        dao.findIngredientByCriteria(
                "cho",
                null,
                "gâteau",
                1,
                10
        ).forEach(i -> System.out.println(i.getName()));
        System.out.println();

        // i) createIngredients OK
        System.out.println("i) createIngredients OK");
        dao.createIngredients(List.of(
                new Ingredient(0, "Fromage", 1200.0, CategoryEnum.DAIRY, null),
                new Ingredient(0, "Oignon", 500.0, CategoryEnum.VEGETABLE, null)
        )).forEach(i -> System.out.println(i.getName()));
        System.out.println();

        // j) createIngredients doublon
        System.out.println("j) createIngredients doublon");
        try {
            dao.createIngredients(List.of(
                    new Ingredient(0, "Carotte", 2000.0, CategoryEnum.VEGETABLE, null),
                    new Ingredient(0, "Laitue", 2000.0, CategoryEnum.VEGETABLE, null)
            ));
        } catch (RuntimeException e) {
            System.out.println("Exception levée ✔");
        }
        System.out.println();

        // k) saveDish création
        System.out.println("k) saveDish création");
        Dish soupe = new Dish(
                0,
                "Soupe de légumes",
                DishEnum.START,
                List.of(new Ingredient(7, null, null, null, null)) // ID réel Oignon
        );
        dao.saveDish(soupe);
        System.out.println("Soupe créée ✔");
        System.out.println();

        // l) saveDish update ajout ingrédients
        System.out.println("l) saveDish update ajout");
        Dish saladeUpdate = new Dish(
                1,
                "Salade fraîche",
                DishEnum.START,
                List.of(
                        new Ingredient(7, null, null, null, null), // Oignon
                        new Ingredient(1, null, null, null, null), // Laitue
                        new Ingredient(2, null, null, null, null), // Tomate
                        new Ingredient(6, null, null, null, null)  // Fromage
                )
        );
        dao.saveDish(saladeUpdate);
        System.out.println("Ingrédients ajoutés ✔");
        System.out.println();

        // m) saveDish suppression ingrédients
        System.out.println("m) saveDish suppression");
        Dish saladeFromage = new Dish(
                1,
                "Salade de fromage",
                DishEnum.START,
                List.of(new Ingredient(6, null, null, null, null)) // Fromage
        );
        dao.saveDish(saladeFromage);
        System.out.println("Ingrédients supprimés ✔");
    }
}
