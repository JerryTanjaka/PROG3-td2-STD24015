package school.hei.prog3td2.model;

import java.time.Instant;

public class StockMovement {
    private Integer id;
    private StockValue value;
    private MovementTypeEnum type;
    private Instant creationDatetime;

    public StockMovement(Integer id, StockValue value,MovementTypeEnum type, Instant creationDatetime) {
        this.id = id;
        this.type = type;
        this.value = value;
        this.creationDatetime = creationDatetime;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MovementTypeEnum getType() {
        return type;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    public StockValue getValue() {
        return value;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }
}