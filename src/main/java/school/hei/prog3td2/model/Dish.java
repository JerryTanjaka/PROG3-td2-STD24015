package school.hei.prog3td2.model;

import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishEnum dishType;
    private List<Ingredient> ingredients;

    public Dish() {}

    public Double getDishCost() {
        if (ingredients == null) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }


    public Dish(int id, String name,DishEnum dishType,  List<Ingredient> ingredients) {
        this.dishType = dishType;
        this.id = id;
        this.ingredients = ingredients;
        this.name = name;
    }

    public DishEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishEnum dishType) {
        this.dishType = dishType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
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
