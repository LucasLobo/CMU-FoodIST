package pt.ulisboa.tecnico.cmov.g16.foodist.model;


import androidx.annotation.NonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MenuItem {

    private Integer id = -1;
    private String name;
    private double price;
    private String description;
    private Set<Integer> imageIds = new HashSet<>();
    private FoodType foodType;

    public MenuItem(Integer id, String name, double price, FoodType foodType, String description, List<Integer> imageIds) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.foodType = foodType;
        this.description = description;
        this.imageIds.addAll(imageIds);
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

    public final Set<Integer> getImageIds() {
        return imageIds;
    }

    public Integer getRandomImageId() {
        int size = imageIds.size();
        if (size == 0) return -1;

        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for (Integer imageId : imageIds) {
            if (i == item) return imageId;
            i++;
        }
        return -1;
    }

    public void addImageId(Integer imageId) {
        imageIds.add(imageId);
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
