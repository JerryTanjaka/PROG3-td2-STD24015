package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Ingredient {
    private Integer id;
    private String name;
    private CategoryEnum category;
    private Double price;

    private List<StockMovement> stockMovementList = new ArrayList<>();


    public Ingredient() {}
    public Ingredient(Integer id, String name, Double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }
    public StockValue getStockValueAt(Instant t) {
        double total = 0.0;
        for (StockMovement m : stockMovementList) {
            if (!m.getCreationDatetime().isAfter(t)) {
                if (m.getType() == MovementTypeEnum.IN) total += m.getValue().getQuantity();
                else total -= m.getValue().getQuantity();
            }
        }
        return new StockValue(total, UnitType.KG);
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