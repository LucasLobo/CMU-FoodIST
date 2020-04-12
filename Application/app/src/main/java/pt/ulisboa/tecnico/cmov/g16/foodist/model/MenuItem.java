package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.media.Image;
import android.widget.ImageView;

import java.util.LinkedList;

public class MenuItem {

    private String name;
    private int price;
    private boolean availability;
    private int discount;
    private String description;
    private FoodCourse foodCourse;
    private TypeOfFood foodType;
    private LinkedList<ImageView> images;


    MenuItem(String name, int price, TypeOfFood foodType, boolean availability, String description, FoodCourse foodCourse) {
        this.name = name;
        this.foodType = foodType;
        this.availability = availability;
        this.price = price;
        this.description = description;
        this.foodCourse = foodCourse;
        images = new LinkedList<>();
    }

    // * GETTERS * //
    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getDiscount() {
        return discount;
    }

    public String getDescription() {
        return description;
    }

    public FoodCourse getFoodCourse() {
        return foodCourse;
    }

    public TypeOfFood getFoodType() {
        return foodType;
    }

    public LinkedList<ImageView> getImages() {
        return images;
    }

    public boolean getAvailability(){
        return availability;
    }


    // * SETTERS * //

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
        discount = 0;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
        price = price * (1-discount);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFoodCourse(FoodCourse foodCourse) {
        this.foodCourse = foodCourse;
    }

    public void setFoodType(TypeOfFood foodType) {
        this.foodType = foodType;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String toString(){
        return ("Food: " + foodType + "\n" + "Price: " + price + " with" + " discount: " + discount + "\n" + description);
    }
}
