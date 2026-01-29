package school.hei.prog3td2.model;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrderList;
    private Integer idTable; // Ou une instance de RestaurantTable
    private Instant arrivalDatetime;
    private Instant departureDatetime;
    private TableOrder tableOrder;
    public TableOrder getTableOrder() { return tableOrder; }
    public void setTableOrder(TableOrder tableOrder) { this.tableOrder = tableOrder; }

    // Ajoute les getters et setters correspondants
    public Order(Instant creationDatetime, List<DishOrder> dishOrderList, Integer id, String reference) {
        this.creationDatetime = creationDatetime;
        this.dishOrderList = dishOrderList;
        this.id = id;
        this.reference = reference;
    }

    public Order(Instant arrivalDatetime, Instant creationDatetime, Instant departureDatetime, List<DishOrder> dishOrderList, Integer id, Integer idTable, String reference) {
        this.arrivalDatetime = arrivalDatetime;
        this.creationDatetime = creationDatetime;
        this.departureDatetime = departureDatetime;
        this.dishOrderList = dishOrderList;
        this.id = id;
        this.idTable = idTable;
        this.reference = reference;
    }

    public Instant getArrivalDatetime() {
        return arrivalDatetime;
    }

    public void setArrivalDatetime(Instant arrivalDatetime) {
        this.arrivalDatetime = arrivalDatetime;
    }

    public Instant getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(Instant departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public Integer getIdTable() {
        return idTable;
    }

    public void setIdTable(Integer idTable) {
        this.idTable = idTable;
    }

    public Order(){

    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public List<DishOrder> getDishOrderList() {
        return dishOrderList;
    }

    public void setDishOrderList(List<DishOrder> dishOrderList) {
        this.dishOrderList = dishOrderList;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDatetime=" + creationDatetime +
                ", dishOrderList=" + dishOrderList +
                '}';
    }

    Double getTotalAmountWithoutVat() {
        throw new RuntimeException("Not implemented");
    }

    Double getTotalAmountWithVat() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order order)) return false;
        return Objects.equals(id, order.id) && Objects.equals(reference, order.reference) && Objects.equals(creationDatetime, order.creationDatetime) && Objects.equals(dishOrderList, order.dishOrderList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDatetime, dishOrderList);
    }
}