package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.List;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrderList;

    // On ne garde que l'objet complexe pour la POO
    private TableOrder tableOrder;

    public Order() {}

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }

    public List<DishOrder> getDishOrderList() { return dishOrderList; }
    public void setDishOrderList(List<DishOrder> dishOrderList) { this.dishOrderList = dishOrderList; }

    public TableOrder getTableOrder() { return tableOrder; }
    public void setTableOrder(TableOrder tableOrder) { this.tableOrder = tableOrder; }
}