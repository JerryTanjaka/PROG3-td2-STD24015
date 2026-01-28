package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Order {
    private int id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders;

    public Order(int id, String reference, Instant creationDatetime, List<DishOrder> dishOrders) {
        this.id = id;
        this.reference = reference;
        this.creationDatetime = creationDatetime;
        this.dishOrders = dishOrders;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && Objects.equals(reference, order.reference) && Objects.equals(creationDatetime, order.creationDatetime) && Objects.equals(dishOrders, order.dishOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDatetime, dishOrders);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", dishOrders=" + dishOrders +
                '}';
    }

    /* Amount calcul */
    public Double getTotalAmountWithoutVAT(){
        double totalAmount = 0;
        for (DishOrder dishOrder : dishOrders) {
            totalAmount += dishOrder.getDish().getPrice() * dishOrder.getQuantity();
        }
        return totalAmount;
    }

    public Double getTotalAmountWithVAT(){
        return getTotalAmountWithoutVAT() + (getTotalAmountWithoutVAT() * 0.2);
    }
}
