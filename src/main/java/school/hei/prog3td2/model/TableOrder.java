package school.hei.prog3td2.model;

import java.time.Instant;

public class TableOrder {
    private RestaurantTable table;
    private Instant arrivalDatetime;
    private Instant departureDatetime;

    public TableOrder(RestaurantTable table, Instant arrival, Instant departure) {
        this.table = table;
        this.arrivalDatetime = arrival;
        this.departureDatetime = departure;
    }

    // Getters
    public RestaurantTable getTable() { return table; }
    public Instant getArrivalDatetime() { return arrivalDatetime; }
    public Instant getDepartureDatetime() { return departureDatetime; }
}