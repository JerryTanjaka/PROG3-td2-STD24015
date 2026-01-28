package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.List;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders;

    public Order() {}

    public Double getTotalAmountWithoutVAT() {
        if (dishOrders == null) return 0.0;
        return dishOrders.stream()
                .mapToDouble(doItem -> doItem.getDish().getPrice() * doItem.getQuantity())
                .sum();
    }

    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() * 1.20;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }
    public List<DishOrder> getDishOrders() { return dishOrders; }
    public void setDishOrders(List<DishOrder> dishOrders) { this.dishOrders = dishOrders; }
}