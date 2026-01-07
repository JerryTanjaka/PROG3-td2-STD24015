package school.hei.prog3td2.model;

import java.util.List;

public class Dish {
    private int id;
    private String name;
    private DishEnum dishType;
    private List<Ingredient> ingredients;
    private Double price;

    public Dish() {}

    public Double getDishCost() {
        if (ingredients == null) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public Dish(int id, String name, DishEnum dishType, Double price, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
        this.ingredients = ingredients;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException(
                    "Impossible de calculer la marge : le prix de vente n'est pas défini"
            );
        }
        return price - getDishCost();
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
