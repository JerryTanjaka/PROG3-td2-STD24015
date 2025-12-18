package school.hei.prog3td2.model;

import java.util.Objects;

public class Ingredient {
    private int id;
    private String name;
    private Double price;
    private CategoryEnum categoryEnum;
    private Dish dish;
    public String getDishName (){

        return dish == null ? null :dish.getName();
    }

    public Ingredient(int id,String name,Double price,CategoryEnum categoryEnum, Dish dish  ) {
        this.categoryEnum = categoryEnum;
        this.dish = dish;
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public CategoryEnum getCategoryEnum() {
        return categoryEnum;
    }

    public void setCategoryEnum(CategoryEnum categoryEnum) {
        this.categoryEnum = categoryEnum;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ingredients{" +
                "categoryEnum=" + categoryEnum +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", dishName=" + getDishName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(price, that.price) && categoryEnum == that.categoryEnum && Objects.equals(dish, that.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, categoryEnum, dish);
    }
}
