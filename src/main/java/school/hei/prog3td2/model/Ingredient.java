package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;

    // TD4: Liste des mouvements de stock
    private List<StockMovement> stockMovementList = new ArrayList<>();

    // TD4: Calcul du stock à un instant T
    public StockValue getStockValueAt(Instant t) {
        double totalQuantity = 0.0;
        for (StockMovement movement : stockMovementList) {
            // Si le mouvement a eu lieu avant ou à l'instant t
            if (!movement.getCreationDatetime().isAfter(t)) {
                if (movement.getType() == MovementTypeEnum.IN) {
                    totalQuantity += movement.getValue().getQuantity();
                } else if (movement.getType() == MovementTypeEnum.OUT) {
                    totalQuantity -= movement.getValue().getQuantity();
                }
            }
        }
        // Le PDF précise que pour l'instant tout est en KG
        return new StockValue(totalQuantity, UnitType.KG);
    }

    // Getters, Setters et Constructeurs habituels...
    public Ingredient() {}
    public Ingredient(Integer id, String name, Double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }
    public List<StockMovement> getStockMovementList() { return stockMovementList; }
    public void setStockMovementList(List<StockMovement> stockMovementList) { this.stockMovementList = stockMovementList; }
}