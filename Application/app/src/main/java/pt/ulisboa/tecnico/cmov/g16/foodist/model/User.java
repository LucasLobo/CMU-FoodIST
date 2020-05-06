package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.location.Location;

import java.util.EnumSet;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class User {

    public enum UserStatus {
        STUDENT(R.string.student),
        PROFESSOR(R.string.professor),
        RESEARCHER(R.string.researcher),
        STAFF(R.string.staff),
        GENERAL_PUBLIC(R.string.generalPublic);

        public int id;

        UserStatus(int id) {
            this.id = id;
        }
    }
    private String username;

    private UserStatus status;
    private CampusLocation location;
    private EnumSet<FoodType> dietaryConstraints = EnumSet.noneOf(FoodType.class);
    private boolean shouldApplyConstraintsFilter = true;
    private boolean locAuto;

    public User() {
        location = new CampusLocation();
        status = UserStatus.GENERAL_PUBLIC;
        username = "NONE";
        locAuto = true; //automatic location finder is on
    }

    public UserStatus getStatus() {
        return status;
    }

    public int getStatusID() {
        return status.id;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public CampusLocation getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location.setLocation(location, true);
    }

    public void setCampus(CampusLocation.Campus campus) {
        location.setCampus(campus);
    }

    public CampusLocation.Campus getCampus() {
        return location.getCampus();
    }

    public int getCampusResourceId() {
        return location.getCampus().id;
    }

    public void addDietaryConstraints(FoodType dietaryConstraints) {
        getDietaryConstraints().add(dietaryConstraints);
    }

    public void removeDietaryConstraints(FoodType dietaryConstraints) {
        getDietaryConstraints().remove(dietaryConstraints);
    }

    public void toggleConstraintsFilter() {
        shouldApplyConstraintsFilter = !shouldApplyConstraintsFilter;
    }

    public boolean shouldApplyConstraintsFilter() {
        return shouldApplyConstraintsFilter;
    }

    public EnumSet<FoodType> getDietaryConstraints() {
        return dietaryConstraints;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLocAuto() {
        return locAuto;
    }

    public void setLocAuto(boolean locAuto) {
        this.locAuto = locAuto;
    }
}
