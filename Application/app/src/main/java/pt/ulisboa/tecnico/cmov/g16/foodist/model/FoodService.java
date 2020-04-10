package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import java.util.ArrayList;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class FoodService {

    private static final String TAG = "FoodService";

    /* Constants for accessibility restrictions */
    public enum AccessRestriction {
        STAIRS(R.string.stairs),
        ELEVATOR(R.string.elevator),
        RAMP(R.string.ramp),
        WHEEL_CHAIR(R.string.wheel_chair);

        public int resourceId;

        AccessRestriction(int id) {
            resourceId = id;
        }
    }

    // Location location
    // OpeningHours openingHours;
    // FoodMenu foodMenu;

    private ArrayList<AccessRestriction> accessRestrictions = new ArrayList<>();

    private String name;
    private String foodType;

    public FoodService(String name, String foodType) {
        this.name = name;
        this.foodType = foodType;
    }

    public String getName() {
        return name;
    }

    public String getFoodType() {
        return foodType;
    }

    public void addAccessRestriction(AccessRestriction accessRestriction) {
        accessRestrictions.add(accessRestriction);
    }

    public ArrayList<AccessRestriction> getAccessRestrictions() {
        return accessRestrictions;
    }


}
