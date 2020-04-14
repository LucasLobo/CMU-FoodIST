package pt.ulisboa.tecnico.cmov.g16.foodist;

import android.app.Application;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class Data extends Application {

    ArrayList<FoodService> foodServiceList;
    User user;

    @Override
    public void onCreate() {
        super.onCreate();
        this.foodServiceList = new ArrayList<>();

        CampusLocation location = new CampusLocation(CampusLocation.Campus.ALAMEDA);
        FoodService foodService = new FoodService("Bar 0", location, "Sanduiches");
        foodService.addAccessRestriction(FoodService.AccessRestriction.ELEVATOR);
        foodService.addAccessRestriction(FoodService.AccessRestriction.RAMP);
        foodServiceList.add(foodService);

        location = new CampusLocation(CampusLocation.Campus.TAGUS);
        foodService = new FoodService("Refeitório 1", location, "Comida variada");
        foodService.addAccessRestriction(FoodService.AccessRestriction.STAIRS);
        foodServiceList.add(foodService);

        for (int i = 2; i < 50; i++) {
            location = new CampusLocation(CampusLocation.Campus.ALAMEDA);
            foodServiceList.add(new FoodService("Food Service " + i, location, "Something"));
        }

        user = new User();
    }

    public ArrayList<FoodService> getFoodServiceList() {
        return foodServiceList;
    }

    public FoodService getFoodService(Integer index) {
        return foodServiceList.get(index);
    }

    public User getUser() {
        return user;
    }
}
