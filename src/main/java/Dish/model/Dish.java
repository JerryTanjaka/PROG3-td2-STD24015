package Dish.model;

import java.util.List;

public class Dish {
    private int id;
    private String name;
    private dishType dishType;
    private List<Ingredients> ingredients;

    public Dish(dishType dishType, int id, List<Ingredients> ingredients, String name) {
        this.dishType = dishType;
        this.id = id;
        this.ingredients = ingredients;
        this.name = name;
    }

    public dishType getDishType() {
        return dishType;
    }

    public void setDishType(dishType dishType) {
        this.dishType = dishType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "dishType=" + dishType +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }
}
