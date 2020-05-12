package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

public class MenuItem {

    private Integer id = -1;
    private String name;
    private double price;
    private String description;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private FoodType foodType;

    public MenuItem(Integer id, String name, double price, FoodType foodType, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.foodType = foodType;
        this.description = description;
    }

    public MenuItem(String name, double price, FoodType foodType, String description) {
        this.name = name;
        this.foodType = foodType;
        this.price = price;
        this.description = description;
    }

    // * GETTERS * //

    public Integer getId() {
        return id;
    }

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

    public void addImage(Bitmap bitmap) {
        images.add(bitmap);
    }

    public Bitmap getImage(Integer index) {
        return images.get(index);
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void setImages(ArrayList<Bitmap> images) {
        this.images = images;
    }

    // * SETTERS * //

    public void setName(String name) {
        this.name = name;
    }


    public void setId(Integer id) {
        this.id = id;
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
