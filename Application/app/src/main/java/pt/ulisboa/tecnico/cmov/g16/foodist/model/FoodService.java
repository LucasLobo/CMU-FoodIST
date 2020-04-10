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

    public FoodService(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAccessRestriction(AccessRestriction accessRestriction) {
        accessRestrictions.add(accessRestriction);
    }

    public ArrayList<AccessRestriction> getAccessRestrictions() {
        return accessRestrictions;
    }


}
