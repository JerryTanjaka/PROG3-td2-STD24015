package school.hei.prog3td2.DAO;

import school.hei.prog3td2.model.CategoryEnum;
import school.hei.prog3td2.model.Dish;
import school.hei.prog3td2.model.DishEnum;
import school.hei.prog3td2.model.Ingredient;
import school.hei.prog3td2.util.DBConnection;

import java.sql.*;
import java.util.*;

public class DataRetriever {

    DBConnection dbConnection = new DBConnection();

    public Dish findDishById(Integer id) {

        String sql = """
            SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type AS dish_type,
                   i.id AS ingredient_id, i.name AS ingredient_name,
                   i.price AS ingredient_price, i.category AS ingredient_category
            FROM dish d
            LEFT JOIN ingredient i ON d.id = i.id_dish
            WHERE d.id = ?
            """;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbConnection.getDBConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

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
            if (dish == null) {
                throw new RuntimeException("Dish not found with id " + id);
            }
            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ignored) {}
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {

        String sql = """
        SELECT i.id AS ingredient_id, i.name AS ingredient_name,
               i.price AS ingredient_price, i.category AS ingredient_category,
               d.id AS dish_id, d.name AS dish_name, d.dish_type AS dish_type
        FROM ingredient i
        LEFT JOIN dish d ON i.id_dish = d.id
        LIMIT ? OFFSET ?
        """;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        List<Ingredient> ingredients = new ArrayList<>();

        try {
            connection = dbConnection.getDBConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, size);
            statement.setInt(2, (page - 1) * size);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Dish dish = null;

                int dishId = resultSet.getInt("dish_id");
                String dishName = resultSet.getString("dish_name");
                String dishTypeStr = resultSet.getString("dish_type");

                if (dishId != 0 && dishTypeStr != null) {
                    dish = new Dish(
                            dishId,
                            dishName,
                            DishEnum.valueOf(dishTypeStr),
                            null
                    );
                }

                Ingredient ingredient = new Ingredient(
                        resultSet.getInt("ingredient_id"),
                        resultSet.getString("ingredient_name"),
                        resultSet.getDouble("ingredient_price"),
                        CategoryEnum.valueOf(resultSet.getString("ingredient_category")),
                        dish
                );

                ingredients.add(ingredient);
            }

            return ingredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException ignored) {}
        }
    }


    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {

        String selectSql = "SELECT name FROM ingredient";
        String insertSql = """
            INSERT INTO ingredient (name, price, category, id_dish)
            VALUES (?, ?, ?::category_type, NULL)
            """;

        Connection connection = null;
        PreparedStatement selectPs = null;
        PreparedStatement insertPs = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getDBConnection();
            connection.setAutoCommit(false);

            Set<String> existingNames = new HashSet<>();

            selectPs = connection.prepareStatement(selectSql);
            rs = selectPs.executeQuery();
            while (rs.next()) {
                existingNames.add(rs.getString("name").toLowerCase());
            }

            for (Ingredient ing : newIngredients) {
                if (existingNames.contains(ing.getName().toLowerCase())) {
                    throw new RuntimeException("Ingredient already exists: " + ing.getName());
                }
            }

            insertPs = connection.prepareStatement(insertSql);
            for (Ingredient ing : newIngredients) {
                insertPs.setString(1, ing.getName());
                insertPs.setDouble(2, ing.getPrice());
                insertPs.setString(3, ing.getCategoryEnum().name());
                insertPs.addBatch();
            }
            insertPs.executeBatch();
            connection.commit();
            return newIngredients;

        } catch (Exception e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (selectPs != null) selectPs.close();
                if (insertPs != null) insertPs.close();
                if (connection != null) connection.close();
            } catch (SQLException ignored) {}
        }
    }

    public Dish saveDish(Dish dishToSave) {
        String findDishSql = """
            SELECT id FROM dish WHERE id = ?
            """;
        String insertDishSql = """
            INSERT INTO dish (name, dish_type)
            VALUES (?, ?::dish_type)
            RETURNING id
            """;

        String updateDishSql = """
            UPDATE dish SET name = ?, dish_type = ?::dish_type WHERE id = ?
            """;

        String clearIngredientsSql = """
            UPDATE ingredient SET id_dish = NULL WHERE id_dish = ?
            """;

        String linkIngredientSql = """
            UPDATE ingredient SET id_dish = ? WHERE id = ?
            """;

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = dbConnection.getDBConnection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(findDishSql);
            ps.setInt(1,dishToSave.getId());
            rs = ps.executeQuery();
            boolean dishexistindb = false;
            dishexistindb = rs.next();
            if (!dishexistindb) {
                ps = connection.prepareStatement(insertDishSql);
                ps.setString(1, dishToSave.getName());
                ps.setString(2, dishToSave.getDishType().name());
                rs = ps.executeQuery();
                if (rs.next()) {
                    dishToSave.setId(rs.getInt("id"));
                }
                rs.close();
                ps.close();
            } else {
                ps = connection.prepareStatement(updateDishSql);
                ps.setString(1, dishToSave.getName());
                ps.setString(2, dishToSave.getDishType().name());
                ps.setInt(3, dishToSave.getId());
                ps.executeUpdate();
                ps.close();
            }

            ps = connection.prepareStatement(clearIngredientsSql);
            ps.setInt(1, dishToSave.getId());
            ps.executeUpdate();
            ps.close();

            if (dishToSave.getIngredients() != null) {
                ps = connection.prepareStatement(linkIngredientSql);
                for (Ingredient ingredient : dishToSave.getIngredients()) {
                    ps.setInt(1, dishToSave.getId());
                    ps.setInt(2, ingredient.getId());
                    ps.executeUpdate();
                }
                ps.close();
            }

            connection.commit();
            return dishToSave;

        } catch (Exception e) {
            try { if (connection != null) connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException ignored) {}
        }
    }

    public List<Dish> findDishByIngredientName(String ingredientName) {

        String sql = """
            SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type AS dish_type
            FROM dish d
            JOIN ingredient i ON d.id = i.id_dish
            WHERE i.name = ?
            """;

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<Dish> dishes = new ArrayList<>();

        try {
            connection = dbConnection.getDBConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, ingredientName);
            rs = ps.executeQuery();

            while (rs.next()) {
                dishes.add(new Dish(
                        rs.getInt("dish_id"),
                        rs.getString("dish_name"),
                        DishEnum.valueOf(rs.getString("dish_type")),
                        null
                ));
            }
            return dishes;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (connection != null) connection.close();
            } catch (SQLException ignored){}
        }
    }


    public List<Dish> findDishesByIngredientName(String ingredientName) {

        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = """
            SELECT DISTINCT d.id, d.name, d.dish_type
            FROM dish d
            JOIN ingredient i ON d.id = i.id_dish
            WHERE i.name ILIKE ?
            """;

        List<Dish> dishes = new ArrayList<>();

        try {
            con = dbConnection.getDBConnection();
            stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + ingredientName + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(
                        DishEnum.valueOf(rs.getString("dish_type"))
                );

                dishes.add(dish);
            }
            return dishes;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public List<Ingredient> findIngredientByCriteria(String ingredientName, CategoryEnum category, String dishName, int page, int size) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;


        StringBuilder sql = new StringBuilder("""
            SELECT i.id, i.name, i.price, i.category
            FROM ingredient i
            LEFT JOIN dish d ON i.id_dish = d.id
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (ingredientName != null) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?::category_type");
            params.add(category.toString());
        }

        if (dishName != null) {
            sql.append(" AND d.name ILIKE ?");
            params.add("%" + dishName + "%");
        }

        sql.append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add(size * (page - 1));

        List<Ingredient> ingredients = new ArrayList<>();

        try {
            con = dbConnection.getDBConnection();
            stmt = con.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                ingredients.add(
                        new Ingredient(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("price"),
                                CategoryEnum.valueOf(rs.getString("category")),
                                null
                        )
                );
            }
            return ingredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (con != null) con.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
