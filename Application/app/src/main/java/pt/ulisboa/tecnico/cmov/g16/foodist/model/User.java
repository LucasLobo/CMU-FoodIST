package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.location.Location;

import java.util.EnumSet;
import java.util.HashMap;

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

    private String username = null;

    private String password = null;

    // FoodServiceId, UserQueueId
    // The server returns a userQueueId when the user joins a queue.
    // User should send a leave Queue request to about these queues after not hearing from a beacon
    // for  a given amount of time
    private HashMap<Integer, Integer> activeQueues;

    private UserStatus status;
    private CampusLocation location;
    private EnumSet<FoodType> dietaryConstraints = EnumSet.noneOf(FoodType.class);
    private boolean shouldApplyConstraintsFilter = true;
    private boolean locAuto;

    public User() {
        location = new CampusLocation();
        status = UserStatus.GENERAL_PUBLIC;
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

    public void setDietaryConstraints(EnumSet<FoodType> constraints) {
        this.dietaryConstraints = constraints;
    }

    public EnumSet<FoodType> getDietaryConstraints() {
        return dietaryConstraints;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLocAuto() {
        return locAuto;
    }

    public void setLocAuto(boolean locAuto) {
        this.locAuto = locAuto;
    }

    public void login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void logout() {
        this.username = null;
        this.password = null;
    }

    public boolean isLoggedIn() {
        return username != null && password != null;
    }
}
