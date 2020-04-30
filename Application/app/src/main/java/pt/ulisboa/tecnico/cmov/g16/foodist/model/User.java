package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.location.Location;
import java.util.ArrayList;
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

    public enum UserDietary {
        MEAT(R.string.meat),
        FISH(R.string.fish),
        VEGETARIAN(R.string.vegetarian),
        VEGAN(R.string.vegan);

        public int id;

        UserDietary(int id) {
            this.id = id;
        }
    }

    private UserStatus status;
    private CampusLocation location;
    private ArrayList<UserDietary> dietaryConstraints = new ArrayList<>();
    private String username;

    public User() {
        location = new CampusLocation();
        status = UserStatus.GENERAL_PUBLIC;
        username = "NONE";
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


    public void addDietaryConstraints(UserDietary dietaryConstraints) {
        getDietaryConstraints().add(dietaryConstraints);
    }

    public void removeDietaryConstraints(UserDietary dietaryConstraints) {
        getDietaryConstraints().remove(dietaryConstraints);
    }

    public ArrayList<UserDietary> getDietaryConstraints() {
        return dietaryConstraints;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
