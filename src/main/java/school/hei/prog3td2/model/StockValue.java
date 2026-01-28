package school.hei.prog3td2.model;

public class StockValue {
    private double quantity;
    private UnitType unit;

    public StockValue(double quantity, UnitType unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public double getQuantity() { return quantity; }
    public UnitType getUnit() { return unit; }
}