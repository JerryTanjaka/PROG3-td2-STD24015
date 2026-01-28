package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public Ingredient() {}
    public Ingredient(Integer id, String name, Double price, CategoryEnum category) {
        this.id = id; this.name = name; this.price = price; this.category = category;
    }

    // LOGIQUE CRUCIALE : On passe le convertisseur pour normaliser l'historique
    public StockValue getStockValueAt(Instant t, UnitConverter converter) {
        double totalKg = 0.0;
        for (StockMovement m : stockMovementList) {
            if (!m.getCreationDatetime().isAfter(t)) {
                // Conversion unifiée en KG pour chaque mouvement
                double qtyKg = converter.convert(this.name, m.getValue().getQuantity(), m.getValue().getUnit(), UnitType.KG);
                if (m.getType() == MovementTypeEnum.IN) totalKg += qtyKg;
                else totalKg -= qtyKg;
            }
        }
        return new StockValue(totalKg, UnitType.KG);
    }

    // Getters/Setters
    public String getName() { return name; }
    public void setStockMovementList(List<StockMovement> list) { this.stockMovementList = list; }
    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }
}