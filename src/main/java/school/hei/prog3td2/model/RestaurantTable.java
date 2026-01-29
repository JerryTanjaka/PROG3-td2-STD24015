package school.hei.prog3td2.model;

import java.time.Instant;
import java.util.List;

public class RestaurantTable {
    private Integer id;
    private Integer number;
    private List<Order> orders; // Optionnel pour le bonus

    public boolean isAvailableAt(Instant t) {
        if (orders == null) return true;
        for (Order o : orders) {
            TableOrder to = o.getTableOrder();
            // Si t est entre l'arrivée et le départ d'une commande existante
            if (!t.isBefore(to.getArrivalDatetime()) && t.isBefore(to.getDepartureDatetime())) {
                return false;
            }
        }
        return true;
    }

    public RestaurantTable(Integer id, Integer number, List<Order> orders) {
        this.id = id;
        this.number = number;
        this.orders = orders;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public List<Order> getOrders() {
        return orders;
    }
}