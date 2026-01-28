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

    // Version avec conversion intégrée pour le calcul historique
    public StockValue getStockValueAt(Instant t, UnitConverter converter) {
        double totalKg = 0.0;
        for (StockMovement m : stockMovementList) {
            if (!m.getCreationDatetime().isAfter(t)) {
                // On normalise en KG chaque mouvement (L, PCS ou KG)
                double qtyInKg = converter.convert(this.name, m.getValue().getQuantity(), m.getValue().getUnit(), UnitType.KG);
                if (m.getType() == MovementTypeEnum.IN) totalKg += qtyInKg;
                else totalKg -= qtyInKg;
            }
        }
        return new StockValue(totalKg, UnitType.KG);
    }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> list) { this.stockMovementList = list; }
    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }
}