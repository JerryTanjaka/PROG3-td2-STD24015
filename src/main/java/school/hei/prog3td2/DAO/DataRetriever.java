package school.hei.prog3td2.DAO;

import school.hei.prog3td2.model.*;
import school.hei.prog3td2.util.DBConnection;
import java.sql.*;
import java.time.Instant;
import java.util.*;

public class DataRetriever {

    public Order saveOrder(Order orderToSave) {
        UnitConverter converter = new UnitConverter();
        Connection conn = new DBConnection().getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Vérification des stocks avec conversion unifiée
            Map<Integer, Double> totalNeededInKg = new HashMap<>();
            for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                Dish dish = findDishById(dishOrder.getDish().getId());
                for (DishIngredient di : dish.getDishIngredientList()) {
                    Ingredient ing = di.getIngredient();
                    double reqKg = converter.convert(ing.getName(), di.getQuantity_required(), di.getUnit(), UnitType.KG);
                    totalNeededInKg.merge(ing.getId(), reqKg * dishOrder.getQuantity(), Double::sum);
                }
            }

            for (Map.Entry<Integer, Double> entry : totalNeededInKg.entrySet()) {
                Ingredient ing = findIngredientById(entry.getKey());
                double availableKg = ing.getStockValueAt(Instant.now(), converter).getQuantity();
                if (availableKg < entry.getValue()) {
                    throw new RuntimeException("Stock insuffisant pour " + ing.getName()
                            + " (Requis: " + entry.getValue() + "kg, Dispo: " + availableKg + "kg)");
                }
            }

            // 2. Sauvegarde en DB (Order -> DishOrder)
            String sqlO = "INSERT INTO \"order\" (reference, creation_datetime) VALUES (?, ?) RETURNING id";
            try (PreparedStatement ps = conn.prepareStatement(sqlO)) {
                ps.setString(1, orderToSave.getReference());
                ps.setTimestamp(2, Timestamp.from(orderToSave.getCreationDatetime()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) orderToSave.setId(rs.getInt(1));
            }

            String sqlD = "INSERT INTO dish_order (id_order, id_dish, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlD)) {
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
        } finally { new DBConnection().closeConnection(conn); }
    }

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
                d.setPrice(rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
                d.setDishIngredientList(getLinkedDishIngredients(d));
                return d;
            }
            throw new RuntimeException("Dish non trouvé");
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<DishIngredient> getLinkedDishIngredients(Dish dish) {
        List<DishIngredient> list = new ArrayList<>();
        String sql = "SELECT di.*, i.name as ing_name, i.price as ing_price, i.category as ing_cat FROM dish_ingredient di JOIN ingredient i ON di.id_ingredient = i.id WHERE di.id_dish = ?";
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dish.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient(rs.getInt("id_ingredient"), rs.getString("ing_name"), rs.getDouble("ing_price"), CategoryEnum.valueOf(rs.getString("ing_cat")));
                list.add(new DishIngredient(rs.getInt("id"), dish, rs.getDouble("quantity_required"), UnitType.valueOf(rs.getString("unit")), ing));
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
                Ingredient i = new Ingredient(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), CategoryEnum.valueOf(rs.getString("category")));
                i.setStockMovementList(getMovementsByIngredientId(id));
                return i;
            }
            throw new RuntimeException("Ingredient non trouvé");
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
                movements.add(new StockMovement(rs.getInt("id"), new StockValue(rs.getDouble("quantity"), UnitType.valueOf(rs.getString("unit"))), MovementTypeEnum.valueOf(rs.getString("type")), rs.getTimestamp("creation_datetime").toInstant()));
            }
            return movements;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}