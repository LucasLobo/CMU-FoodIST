package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public enum FoodType {
    MEAT(R.string.meat), FISH(R.string.fish), VEGETARIAN(R.string.vegetarian), VEGAN(R.string.vegan);


    public int resourceId;

    FoodType(int id){
        resourceId = id;
    }
}
