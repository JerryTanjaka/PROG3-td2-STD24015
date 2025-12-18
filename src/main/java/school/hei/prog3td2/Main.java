package school.hei.prog3td2;

import school.hei.prog3td2.DAO.DataRetriever;
import school.hei.prog3td2.model.CategoryEnum;
import school.hei.prog3td2.model.Ingredient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        Ingredient ing1 = new Ingredient(10,"epice",12.1, CategoryEnum.VEGETABLE,null);
        Ingredient ing2 = new Ingredient(11,"cactus",13.2, CategoryEnum.VEGETABLE,null);
        List<Ingredient> addListIngredient = List.of(ing1,ing2);
        DataRetriever dataRetriever = new DataRetriever();
//    System.out.println(dataRetriever.findDishById(1));
        System.out.println(dataRetriever.findIngredients(1,12));
       System.out.println(dataRetriever.createIngredients(addListIngredient));
    }
}
