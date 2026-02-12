package school.hei.prog3td2.DAO;
import school.hei.prog3td2.model.CategoryEnum;
import school.hei.prog3td2.model.Dish;
import school.hei.prog3td2.model.DishIngredient;
import school.hei.prog3td2.model.DishOrder;
import school.hei.prog3td2.model.DishTypeEnum;
import school.hei.prog3td2.model.Ingredient;
import school.hei.prog3td2.model.MovementTypeEnum;
import school.hei.prog3td2.model.Order;
import school.hei.prog3td2.model.StockMovement;
import school.hei.prog3td2.model.StockValue;
import school.hei.prog3td2.model.Unit;
import school.hei.prog3td2.model.UnitConverter;
import school.hei.prog3td2.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataRetriever {
    public StockValue getStockValueAt(Instant t, Integer ingredientId) {
        String sql = """
            SELECT unit,
                   SUM(CASE WHEN type = 'OUT' THEN -quantity ELSE quantity END) as actual_quantity
            FROM stock_movement
            WHERE id_ingredient = ? AND creation_datetime <= ?
            GROUP BY unit;
            """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, ingredientId);
            preparedStatement.setTimestamp(2, Timestamp.from(t));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                StockValue stockValue = new StockValue();
                stockValue.setQuantity(resultSet.getDouble("actual_quantity"));
                stockValue.setUnit(Unit.valueOf(resultSet.getString("unit")));
                return stockValue;
            }

            return new StockValue(0.0, Unit.KG);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du stock via SQL", e);
        }
    }
    public Double getDishCostSQL(Integer dishId) {

        String sql = """
        SELECT SUM(di.required_quantity * i.price) as total_cost
        FROM dish_ingredient di
        JOIN ingredient i ON di.id_ingredient = i.id
        WHERE di.id_dish = ?;
    """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dishId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_cost");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Double getGrossMarginSQL(Integer dishId) {
        String sql = """
        SELECT (d.selling_price - SUM(di.required_quantity * i.price)) as margin
        FROM dish d
        JOIN dish_ingredient di ON d.id = di.id_dish
        JOIN ingredient i ON di.id_ingredient = i.id
        WHERE d.id = ?
        GROUP BY d.selling_price;
    """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dishId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("margin");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        public List<StockMovement> getStockMovementByIngredientId(Connection conn , int id){
        String sql = """
                select id, id_ingredient, quantity,type,unit, creation_datetime
                from stockmovement
                where id_ingredient = ?;
                """;
        try(PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            List<StockMovement> stockMovements = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockMovement stockMovement = new StockMovement();
                stockMovement.setId(resultSet.getInt("id"));
                StockValue stockValue = new StockValue();
                stockValue.setQuantity(resultSet.getDouble("quantity"));
                stockValue.setUnit(Unit.valueOf(resultSet.getString("unit")));
                stockMovement.setValue(stockValue);
                stockMovement.setType(MovementTypeEnum.valueOf(resultSet.getString("type")));
                stockMovement.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());
                stockMovements.add(stockMovement);
            }
            return stockMovements;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private Ingredient findIngredientById(Connection conn, Integer id) {
        try {
            PreparedStatement ps = conn.prepareStatement("""
            select id, name, price, category
            from ingredient
            where id = ?
        """);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Ingredient not found " + id);
            }

            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getInt("id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setPrice(rs.getDouble("price"));
            ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

            ingredient.setStockMovementList(
                    getStockMovementByIngredientId(conn, id)
            );

            return ingredient;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Order findOrderByReference(String reference) {
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    select id, reference, creation_datetime from "order" where reference like ?""");
            preparedStatement.setString(1, reference);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                Integer idOrder = resultSet.getInt("id");
                order.setId(idOrder);
                order.setReference(resultSet.getString("reference"));
                order.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());
                order.setDishOrderList(findDishOrderByIdOrder(idOrder));
                return order;
            }
            throw new RuntimeException("Order not found with reference " + reference);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DishOrder> findDishOrderByIdOrder(Integer idOrder) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishOrder> dishOrders = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select id, id_dish, quantity from dish_order where dish_order.id_order = ?
                            """);
            preparedStatement.setInt(1, idOrder);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Dish dish = findDishById(resultSet.getInt("id_dish"));
                DishOrder dishOrder = new DishOrder();
                dishOrder.setId(resultSet.getInt("id"));
                dishOrder.setQuantity(resultSet.getInt("quantity"));
                dishOrder.setDish(dish);
                dishOrders.add(dishOrder);
            }
            dbConnection.closeConnection(connection);
            return dishOrders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select dish.id as dish_id, dish.name as dish_name, dish_type, dish.selling_price as dish_price
                            from dish
                            where dish.id = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(resultSet.getString("dish_type")));
                dish.setPrice(resultSet.getObject("dish_price") == null
                        ? null : resultSet.getDouble("dish_price"));
                dish.setDishIngredients(findIngredientByDishId(id));
                return dish;
            }
            dbConnection.closeConnection(connection);
            throw new RuntimeException("Dish not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    Ingredient saveIngredient(Ingredient toSave) {
        String upsertIngredientSql = """
                    INSERT INTO ingredient (id, name, price, category)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        category = EXCLUDED.category,
                        price = EXCLUDED.price
                    RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer ingredientId;
            try (PreparedStatement ps = conn.prepareStatement(upsertIngredientSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                }
                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getCategory().name());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    ingredientId = rs.getInt(1);
                }
            }

            insertIngredientStockMovements(conn, toSave);

            conn.commit();
            return findIngredientById(ingredientId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertIngredientStockMovements(Connection conn, Ingredient ingredient) {
        List<StockMovement> stockMovementList = ingredient.getStockMovementList();
        String sql = """
                insert into stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime)
                values (?, ?, ?, ?::movement_type, ?::unit, ?)
                on conflict (id) do nothing
                """;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            for (StockMovement stockMovement : stockMovementList) {
                if (ingredient.getId() != null) {
                    preparedStatement.setInt(1, ingredient.getId());
                } else {
                    preparedStatement.setInt(1, getNextSerialValue(conn, "stock_movement", "id"));
                }
                preparedStatement.setInt(2, ingredient.getId());
                preparedStatement.setDouble(3, stockMovement.getValue().getQuantity());
                preparedStatement.setObject(4, stockMovement.getType());
                preparedStatement.setObject(5, stockMovement.getValue().getUnit());
                preparedStatement.setTimestamp(6, Timestamp.from(stockMovement.getCreationDatetime()));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Ingredient findIngredientById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("select id, name, price, category from ingredient where id = ?;");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int idIngredient = resultSet.getInt("id");
                String name = resultSet.getString("name");
                CategoryEnum category = CategoryEnum.valueOf(resultSet.getString("category"));
                Double price = resultSet.getDouble("price");
                return new Ingredient(idIngredient, name, category, price, findStockMovementsByIngredientId(idIngredient));
            }
            throw new RuntimeException("Ingredient not found " + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    List<StockMovement> findStockMovementsByIngredientId(Integer id) {

        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<StockMovement> stockMovementList = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select id, quantity, unit, type, creation_datetime
                            from stock_movement
                            where stock_movement.id_ingredient = ?;
                            """);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockMovement stockMovement = new StockMovement();
                stockMovement.setId(resultSet.getInt("id"));
                stockMovement.setType(MovementTypeEnum.valueOf(resultSet.getString("type")));
                stockMovement.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());

                StockValue stockValue = new StockValue();
                stockValue.setQuantity(resultSet.getDouble("quantity"));
                stockValue.setUnit(Unit.valueOf(resultSet.getString("unit")));
                stockMovement.setValue(stockValue);

                stockMovementList.add(stockMovement);
            }
            dbConnection.closeConnection(connection);
            return stockMovementList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type,
                        selling_price = EXCLUDED.selling_price
                    RETURNING id
                """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                if (toSave.getId() != null) {
                    ps.setInt(1, toSave.getId());
                } else {
                    ps.setInt(1, getNextSerialValue(conn, "dish", "id"));
                }
                if (toSave.getPrice() != null) {
                    ps.setDouble(2, toSave.getPrice());
                } else {
                    ps.setNull(2, Types.DOUBLE);
                }
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    dishId = rs.getInt(1);
                }
            }

            List<DishIngredient> newDishIngredients = toSave.getDishIngredients();
            detachIngredients(conn, newDishIngredients);
            attachIngredients(conn, newDishIngredients);

            conn.commit();
            return findDishById(dishId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                        INSERT INTO ingredient (id, name, category, price)
                        VALUES (?, ?, ?::ingredient_category, ?)
                        RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }


    private void detachIngredients(Connection conn, List<DishIngredient> dishIngredients) {
        Map<Integer, List<DishIngredient>> dishIngredientsGroupByDishId = dishIngredients.stream()
                .collect(Collectors.groupingBy(dishIngredient -> dishIngredient.getDish().getId()));
        dishIngredientsGroupByDishId.forEach((dishId, dishIngredientList) -> {
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM dish_ingredient where id_dish = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate(); // TODO: must be a grouped by batch
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void attachIngredients(Connection conn, List<DishIngredient> ingredients)
            throws SQLException {

        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }
        String attachSql = """
                    insert into dish_ingredient (id, id_ingredient, id_dish, required_quantity, unit)
                    values (?, ?, ?, ?, ?::unit)
                """;

        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (DishIngredient dishIngredient : ingredients) {
                ps.setInt(1, getNextSerialValue(conn, "dish_ingredient", "id"));
                ps.setInt(2, dishIngredient.getIngredient().getId());
                ps.setInt(3, dishIngredient.getDish().getId());
                ps.setDouble(4, dishIngredient.getQuantity());
                ps.setObject(5, dishIngredient.getUnit());
                ps.addBatch(); // Can be substitute ps.executeUpdate() but bad performance
            }
            ps.executeBatch();
        }
    }

    private List<DishIngredient> findIngredientByDishId(Integer idDish) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishIngredient> dishIngredients = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select ingredient.id, ingredient.name, ingredient.price, ingredient.category, di.required_quantity, di.unit
                            from ingredient join dish_ingredient di on di.id_ingredient = ingredient.id where id_dish = ?;
                            """);
            preparedStatement.setInt(1, idDish);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setPrice(resultSet.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(resultSet.getString("category")));

                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setIngredient(ingredient);
                dishIngredient.setQuantity(resultSet.getObject("required_quantity") == null ? null : resultSet.getDouble("required_quantity"));
                dishIngredient.setUnit(Unit.valueOf(resultSet.getString("unit")));

                dishIngredients.add(dishIngredient);
            }
            dbConnection.closeConnection(connection);
            return dishIngredients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
    public Order saveOrder(Order orderToSave) throws SQLException {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        /*check if stock ingredient is sufficient */
        for(DishOrder dishOrder: orderToSave.getDishOrderList()){
            for (DishIngredient dishIng: dishOrder.getDish().getDishIngredients()){
                double requiredQuantity = UnitConverter.convert(dishIng.getIngredient().getName(), dishIng.getQuantity(),dishIng.getUnit() , Unit.KG ) * dishOrder.getQuantity();
                Ingredient ingredient = findIngredientById(connection,dishIng.getIngredient().getId());
                double availableQuantity = ingredient.getStockValueAt(Instant.now()).getQuantity();
                if(availableQuantity < requiredQuantity){
                    throw new RuntimeException("Insufficient stock for ingredient: " + ingredient.getName());
                }
            }
        }
        String sql = """
                insert into "Order" (id, reference, creation_datetime)
                values (?, ?, ?)
                """;

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);

            /* Insert dishorder */

            int orderId;
            if(orderToSave.getId() != null){
                orderId = orderToSave.getId();
            } else {
                orderId = getNextSerialValue(connection, "Order", "id");
            }
            ps.setInt(1, orderId);

            ps.setString(2, orderToSave.getReference());
            ps.setTimestamp(3, Timestamp.from(orderToSave.getCreationDatetime()));
            ps.executeUpdate();
            try{
                saveDishOrder(connection, orderToSave.getDishOrderList(), orderId);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            connection.commit();
            dbConnection.closeConnection(connection);
            return orderToSave;
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        }finally {
            dbConnection.closeConnection(connection);
        }

    }

    public void saveDishOrder(Connection conn, List<DishOrder> dishOrders, int orderId) throws SQLException {
        if (dishOrders.isEmpty()){
            throw new RuntimeException("No dish order found");
        }
        String dishOrderSql = """
                insert into dishorder (id, id_order, id_dish, quantity) values
                (? , ? ,? ,?)
                returning id;
                """;
        try{
            PreparedStatement ps = conn.prepareStatement(dishOrderSql);
            for (DishOrder dishOrder: dishOrders){
                if(dishOrder.getId() != null){
                    ps.setInt(1, dishOrder.getId());

                }
                else{
                    ps.setInt(1, getNextSerialValue(conn, "dishorder", "id"));
                }
                ps.setInt(2 , orderId);
                ps.setInt(3 , dishOrder.getDish().getId());
                ps.setInt(4, dishOrder.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    };

    public List<DishOrder> findDishOrdersByOrderReference(String reference){
        String sql = """
                select dso.id as dish_order_id, dso.id_dish as dish_order_id_dish,
                       dso.quantity as dish_order_quantity , dso.id_order as dish_order_id_order
                from "Order" o
                join dishorder dso on o.id = dso.id_order
                where reference = ?;
                """;
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, reference);
            ResultSet rs = ps.executeQuery();
            List<DishOrder> dishOrders = new ArrayList<>();
            while(rs.next()){
                DishOrder dishOrder = new DishOrder();
                dishOrder.setId(rs.getInt("dish_order_id"));
                Dish dish = findDishById(rs.getInt("dish_order_id_dish"));
                dishOrder.setDish(dish);
                dishOrder.setQuantity(rs.getInt("dish_order_quantity"));
                dishOrders.add(dishOrder);
            }
            dbConnection.closeConnection(connection);
            return dishOrders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            dbConnection.closeConnection(connection);
        }
    }


}