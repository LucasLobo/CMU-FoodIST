package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.LinkedList;

public class MenuItem implements Serializable { //fixme not necessary with current approach

    private String name;
    private double price;
    private String description;
    private transient LinkedList<ImageView> images;
    private FoodType foodType;


    public MenuItem(String name, double price, FoodType foodType, String description, LinkedList<ImageView> images) {
        this.name = name;
        this.foodType = foodType;
        this.price = price;
        this.description = description;
        this.images = images;
    }

    // * GETTERS * //
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public LinkedList<ImageView> getImages() {
        return images;
    }


    // * SETTERS * //

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    @NonNull
    public String toString(){
        return ("Food: " + foodType + "\n" + "Price: " + price + "\n" + description);
    }
}
