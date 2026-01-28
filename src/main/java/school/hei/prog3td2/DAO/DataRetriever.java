package school.hei.prog3td2.DAO;

import school.hei.prog3td2.model.*;
import school.hei.prog3td2.util.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.*;

public class DataRetriever {


    public Order saveOrder(Order orderToSave) {
        Connection conn = new DBConnection().getConnection();
        try {
            conn.setAutoCommit(false);

            Map<Integer, Double> totalNeeded = new HashMap<>();
            for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                Dish dish = findDishById(dishOrder.getDish().getId());
                for (DishIngredient di : dish.getDishIngredientList()) {
                    double req = di.getQuantity_required() * dishOrder.getQuantity();
                    totalNeeded.merge(di.getIngredient().getId(), req, Double::sum);
                }
            }

            for (Map.Entry<Integer, Double> entry : totalNeeded.entrySet()) {
                Ingredient ing = findIngredientById(entry.getKey());
                double stock = ing.getStockValueAt(Instant.now()).getQuantity();
                if (stock < entry.getValue()) {
                    throw new RuntimeException("Stock insuffisant pour l'ingrédient : " + ing.getName()
                            + " (Requis: " + entry.getValue() + "kg, Disponible: " + stock + "kg)");
                }
            }

            // b. Sauvegarde Order
            String sqlOrder = "INSERT INTO \"order\" (reference, creation_datetime) VALUES (?, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setString(1, orderToSave.getReference());
                ps.setTimestamp(2, Timestamp.from(orderToSave.getCreationDatetime()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) orderToSave.setId(rs.getInt(1));
            }

            // c. Sauvegarde DishOrders
            String sqlDetail = "INSERT INTO dish_order (id_order, id_dish, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlDetail)) {
                for (DishOrder item : orderToSave.getDishOrders()) {
                    ps.setInt(1, orderToSave.getId());
                    ps.setInt(2, item.getDish().getId());
                    ps.setInt(3, item.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return orderToSave;
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e.getMessage());
        } finally {
            new DBConnection().closeConnection(conn);
        }
    }

    public Order findOrderByReference(String ref) {
        String sql = "SELECT * FROM \"order\" WHERE reference = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ref);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = new Order();
                o.setId(rs.getInt("id"));
                o.setReference(rs.getString("reference"));
                o.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                return o;
            }
            throw new RuntimeException("Commande introuvable : " + ref);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // 2. GESTION DES PLATS (DISH)
    public Dish findDishById(Integer id) {
        String sql = "SELECT * FROM dish WHERE id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Dish d = new Dish();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                d.setPrice(rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
                d.setDishIngredientList(getLinkedDishIngredients(d)); // Utilise l'objet dish pour éviter la récursion
                return d;
            }
            throw new RuntimeException("Dish non trouvé : " + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<DishIngredient> getLinkedDishIngredients(Dish dish) {
        List<DishIngredient> list = new ArrayList<>();
        String sql = """
            SELECT di.*, i.name as ing_name, i.price as ing_price, i.category as ing_cat 
            FROM dish_ingredient di 
            JOIN ingredient i ON di.id_ingredient = i.id 
            WHERE di.id_dish = ?
            """;
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dish.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(
                        rs.getInt("id_ingredient"),
                        rs.getString("ing_name"),
                        rs.getDouble("ing_price"),
                        CategoryEnum.valueOf(rs.getString("ing_cat"))
                );
                list.add(new DishIngredient(
                        rs.getInt("id"),
                        dish,
                        rs.getDouble("quantity_required"),
                        UnitType.valueOf(rs.getString("unit")),
                        ing
                ));
            }
            return list;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Ingredient findIngredientById(Integer id) {
        String sql = "SELECT * FROM ingredient WHERE id = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ingredient i = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        CategoryEnum.valueOf(rs.getString("category"))
                );
                i.setStockMovementList(getMovementsByIngredientId(id));
                return i;
            }
            throw new RuntimeException("Ingredient non trouvé : " + id);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public Ingredient saveIngredient(Ingredient toSave) {
        String upsertSql = """
            INSERT INTO ingredient (id, name, category, price)
            VALUES (?, ?, ?::category_type, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                category = EXCLUDED.category,
                price = EXCLUDED.price
            RETURNING id;
        """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(upsertSql)) {
                if (toSave.getId() == null) ps.setNull(1, Types.INTEGER);
                else ps.setInt(1, toSave.getId());
                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getCategory().name());
                ps.setDouble(4, toSave.getPrice());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) toSave.setId(rs.getInt(1));
            }

            if (toSave.getStockMovementList() != null && !toSave.getStockMovementList().isEmpty()) {
                String sqlMov = "INSERT INTO stock_movement (id_ingredient, quantity, type, unit, creation_datetime) VALUES (?, ?, ?::mouvement_type, ?::unit_type, ?) ON CONFLICT DO NOTHING";
                try (PreparedStatement psM = conn.prepareStatement(sqlMov)) {
                    for (StockMovement m : toSave.getStockMovementList()) {
                        psM.setInt(1, toSave.getId());
                        psM.setDouble(2, m.getValue().getQuantity());
                        psM.setString(3, m.getType().name());
                        psM.setString(4, m.getValue().getUnit().name());
                        psM.setTimestamp(5, Timestamp.from(m.getCreationDatetime()));
                        psM.addBatch();
                    }
                    psM.executeBatch();
                }
            }
            conn.commit();
            return toSave;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<StockMovement> getMovementsByIngredientId(int ingredientId) {
        List<StockMovement> movements = new ArrayList<>();
        String sql = "SELECT * FROM stock_movement WHERE id_ingredient = ? ORDER BY creation_datetime";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                movements.add(new StockMovement(
                        rs.getInt("id"),
                        new StockValue(rs.getDouble("quantity"), UnitType.valueOf(rs.getString("unit"))),
                        MovementTypeEnum.valueOf(rs.getString("type")),
                        rs.getTimestamp("creation_datetime").toInstant()
                ));
            }
            return movements;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Ingredient> createIngredients(List<Ingredient> ingredients) {
        Connection conn = new DBConnection().getConnection();
        try {
            conn.setAutoCommit(false);
            for (Ingredient ing : ingredients) {
                saveIngredient(ing);
            }
            conn.commit();
            return ingredients;
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e);
        }
    }
}