package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import org.threeten.bp.LocalTime;

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

    private OpeningTime openingTime;
    private CampusLocation location;
    // FoodMenu foodMenu;

    private ArrayList<AccessRestriction> accessRestrictions = new ArrayList<>();

    private String name;
    private String foodType;
    private Menu menu;


    public FoodService(String name, CampusLocation location, OpeningTime openingTime, String foodType) {
        this.name = name;
        this.location = location;
        this.openingTime = openingTime;
        this.foodType = foodType;
        menu = new Menu();
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

    public OpeningTime getOpeningTime() {
        return openingTime;
    }

    public Boolean isOpenAt(LocalTime time, User.UserStatus status) {
        return openingTime.isOpenAt(time, status);
    }

    public Boolean isOpen(User.UserStatus status) {
        return openingTime.isOpen(status);
    }

    public ArrayList<AccessRestriction> getAccessRestrictions() {
        return accessRestrictions;
    }

    public CampusLocation getLocation() {
        return location;
    }

    public String getLocatioName() {
        return location.getName();
    }

    public Menu getMenu(){
        return menu;
    }
}
