package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;

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

    private ArrayList<AccessRestriction> accessRestrictions = new ArrayList<>();

    private Integer id;
    private String name;
    private Menu menu;


    public FoodService(Integer id, String name, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.location = new CampusLocation(latitude, longitude);
        this.openingTime = new OpeningTime();
        this.menu = new Menu();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addAccessRestriction(AccessRestriction accessRestriction) {
        accessRestrictions.add(accessRestriction);
    }

    public void addSchedule(User.UserStatus status, Integer openingHour, Integer openingMinute, Integer closingHour, Integer closingMinute) {
        openingTime.addScheduleForStatus(status, openingHour, openingMinute, closingHour, closingMinute);
    }

    public void addSchedule(Integer openingHour, Integer openingMinute, Integer closingHour, Integer closingMinute) {
        for (User.UserStatus status : User.UserStatus.values()) {
            addSchedule(status, openingHour, openingMinute, closingHour, closingMinute);
        }
    }

    public void addMenuItem(String name, double price, FoodType foodType, boolean availability, String description) {
        menu.addMenuItem(name, price, foodType, availability, description);
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

    public EnumSet<FoodType> getFoodTypes() {
        EnumSet<FoodType> set = EnumSet.noneOf(FoodType.class);
        LinkedList<MenuItem> menuList = menu.getMenuList();
        for (MenuItem item : menuList) {
            set.add(item.getFoodType());
        }
        return set;
    }

    public boolean meetsConstraints(EnumSet<FoodType> dietaryConstraints, boolean allowEmpty) {
        if (dietaryConstraints.isEmpty()) return true;

        EnumSet<FoodType> foodServiceFoodTypes = EnumSet.copyOf(getFoodTypes());
        if (allowEmpty && foodServiceFoodTypes.isEmpty()) {
            return true;
        }
        foodServiceFoodTypes.removeAll(dietaryConstraints);
        return !foodServiceFoodTypes.isEmpty();
    }

    public CampusLocation getLocation() {
        return location;
    }

    public CampusLocation.Campus getCampus() {
        return location.getCampus();
    }

    public Menu getMenu(){
        return menu;
    }
}
