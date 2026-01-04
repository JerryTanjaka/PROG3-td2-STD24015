package school.hei.prog3td2.DAO;

import school.hei.prog3td2.model.CategoryEnum;
import school.hei.prog3td2.model.Dish;
import school.hei.prog3td2.model.DishEnum;
import school.hei.prog3td2.model.Ingredient;
import school.hei.prog3td2.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataRetriever {
    DBConnection dbConnection = new DBConnection();

    public Dish findDishById(Integer id) throws SQLException {
        String sql = """
                SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type AS dish_type,
                i.id AS ingredient_id, i.name AS ingredient_name, i.price AS ingredient_price, i.category AS ingredient_category
                FROM dish d
                JOIN ingredient i ON d.id = i.id_dish
                WHERE d.id = ?
                """;

        PreparedStatement statement = dbConnection.getDBConnection().prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        Dish dish = null;
        while (resultSet.next()) {
            if (dish == null) {
                dish = new Dish(
                        resultSet.getInt("dish_id"),
                        resultSet.getString("dish_name"),
                        DishEnum.valueOf(resultSet.getString("dish_type")),
                        new ArrayList<>()
                );
            }
            Ingredient ingredient = new Ingredient(
                    resultSet.getInt("ingredient_id"),
                    resultSet.getString("ingredient_name"),
                    resultSet.getDouble("ingredient_price"),
                    CategoryEnum.valueOf(resultSet.getString("ingredient_category")),
                    dish
            );
            dish.getIngredients().add(ingredient);
        }
        return dish;
    }

    public List<Ingredient> findIngredients(int page, int size) {
        String sql = """
                SELECT i.id AS ingredient_id, i.name AS ingredient_name, i.price AS ingredient_price, i.category AS ingredient_category,
                d.id AS dish_id, d.name AS dish_name, d.dish_type AS dish_type
                FROM ingredient i
                JOIN dish d ON i.id_dish = d.id
                LIMIT ? OFFSET ?
                """;
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            PreparedStatement statement = dbConnection.getDBConnection().prepareStatement(sql);
            statement.setInt(1, size);
            statement.setInt(2, (page - 1) * size);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                Dish dish = new Dish(
                        resultSet.getInt("dish_id"),
                        resultSet.getString("dish_name"),
                        DishEnum.valueOf(resultSet.getString("dish_type")),
                        null
                );
                Ingredient ingredient = new Ingredient(
                        resultSet.getInt("ingredient_id"),
                        resultSet.getString("ingredient_name"),
                        resultSet.getDouble("ingredient_price"),
                        CategoryEnum.valueOf(resultSet.getString("ingredient_category")),
                        dish
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ingredients;
    }


    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        String selectSql = """
        SELECT i.name FROM ingredient i
        """;

        String insertSql = """
        INSERT INTO ingredient (name, price, category, id_dish)
        VALUES (?, ?, ?::category_type, ?)
        """;

        try (Connection connection = dbConnection.getDBConnection()) {
            connection.setAutoCommit(false);
            Set<String> dbIngredientNames = new HashSet<>();
            try (PreparedStatement ps = connection.prepareStatement(selectSql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    dbIngredientNames.add(rs.getString("name").toLowerCase());
                }
            }
            for (Ingredient newIng : newIngredients) {
                if (dbIngredientNames.contains(newIng.getName().toLowerCase())) {
                    throw new RuntimeException(
                            "Ingredient already exists: " + newIng.getName()
                    );
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                for (Ingredient newIng : newIngredients) {
                    ps.setString(1, newIng.getName());
                    ps.setDouble(2, newIng.getPrice());
                    ps.setString(3, newIng.getCategoryEnum().name());
                    ps.setObject(4, null);

                    ps.executeUpdate();
                }
            }
            connection.commit();
            return newIngredients;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Dish saveDish(Dish dishToSave) {
        String insertDishSql = """
        INSERT INTO dish (name, dish_type)
        VALUES (?, ?::dish_type)
        RETURNING id
        """;

        String updateDishSql = """
        UPDATE dish
        SET name = ?, dish_type = ?::dish_type
        WHERE id = ?
        """;

        String clearIngredientsSql = """
        UPDATE ingredient
        SET id_dish = NULL
        WHERE id_dish = ?
        """;

        String linkIngredientSql = """
        UPDATE ingredient
        SET id_dish = ?
        WHERE id = ?
        """;

        try (Connection connection = dbConnection.getDBConnection()) {
            connection.setAutoCommit(false);

            if (dishToSave.getId() == 0) {
                try (PreparedStatement ps = connection.prepareStatement(insertDishSql)) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        dishToSave.setId(rs.getInt("id"));
                    }
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(updateDishSql)) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());
                    ps.setInt(3, dishToSave.getId());
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(clearIngredientsSql)) {
                ps.setInt(1, dishToSave.getId());
                ps.executeUpdate();
            }

            if (dishToSave.getIngredients() != null) {
                try (PreparedStatement ps = connection.prepareStatement(linkIngredientSql)) {
                    for (Ingredient ingredient : dishToSave.getIngredients()) {
                        ps.setInt(1, dishToSave.getId());
                        ps.setInt(2, ingredient.getId());
                        ps.executeUpdate();
                    }
                }
            }

            connection.commit();
            return dishToSave;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
