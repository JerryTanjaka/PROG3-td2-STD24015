package school.hei.prog3td2.model;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class DishIngredient {
    private int id;
    private Dish dish;
    private double quantity_required;
    private UnitType unit;
    private Ingredient ingredient;


    public DishIngredient(int id,Dish dish, double quantity_required, UnitType unit,Ingredient ingredient
    ) {
        this.id= id;
        this.dish = dish;
        this.quantity_required = quantity_required;
        this.unit = unit;
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }


    public double getQuantity_required() {
        return quantity_required;
    }

    public void setQuantity_required(double quantity_required) {
        this.quantity_required = quantity_required;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }
    public double getIngredientCost(){
        if(ingredient.getPrice() == null) throw new RuntimeException("ingredient price is null");
        return ingredient.getPrice() * getQuantity_required();
    }
    @Override
    public String toString() {
        return "DishIngredient{" +
                ", quantity_required=" + quantity_required +
                ", unit=" + unit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return Double.compare(quantity_required, that.quantity_required) == 0 && Objects.equals(dish, that.dish) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dish, quantity_required, unit);
    }
}