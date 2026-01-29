package school.hei.prog3td2.model;

public class RestaurantTable {
    private Integer id;
    private Integer number;

    public RestaurantTable(Integer id, Integer number) {
        this.id = id;
        this.number = number;
    }

    public Integer getId() { return id; }
    public Integer getNumber() { return number; }
}