package pt.ulisboa.tecnico.cmov.g16.foodist;

import android.app.Application;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalTime;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.OpeningTime;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.TypeOfFood;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class Data extends Application {

    private static final String TAG = "Data";

    ArrayList<FoodService> foodServiceList;
    User user;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        initData();
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

    public void initData() {
        this.user = new User();
        this.foodServiceList = new ArrayList<>();

        OpeningTime.Schedule schedule1 = new OpeningTime.Schedule(LocalTime.of(8,0), LocalTime.of(20,0));
        OpeningTime.Schedule schedule2 = new OpeningTime.Schedule(LocalTime.of(10, 0), LocalTime.of(15, 0));

        OpeningTime openingTime = new OpeningTime();
        openingTime.addScheduleStatus(User.UserStatus.STUDENT, schedule1);
        openingTime.addScheduleStatus(User.UserStatus.PROFESSOR, schedule1);
        openingTime.addScheduleStatus(User.UserStatus.RESEARCHER, schedule1);
        openingTime.addScheduleStatus(User.UserStatus.GENERAL_PUBLIC, schedule2);

        CampusLocation location = new CampusLocation(CampusLocation.Campus.ALAMEDA, "Civil");
        FoodService foodService = new FoodService("Bar 0", location, openingTime, "Sanduiches");
        foodService.addAccessRestriction(FoodService.AccessRestriction.ELEVATOR);
        foodService.addAccessRestriction(FoodService.AccessRestriction.RAMP);
        foodService.getMenu().addMenuItem("Menu A", 2, TypeOfFood.MEAT,true, "This menu is consisted in fries and soup");
        foodService.getMenu().addMenuItem("Menu B", 2, TypeOfFood.FISH,true, "This menu is consisted in a main fish course with fries or soup");
        foodService.getMenu().addMenuItem("Menu C", 2, TypeOfFood.VEGAN,true, "This menu is consisted in a main vegan course with fries and fruit");
        foodService.getMenu().addMenuItem("Menu D", 2, TypeOfFood.VEGETARIAN,true, "This menu is consisted in a vegetarian fish course with fries or soup");
        foodServiceList.add(foodService);

        location = new CampusLocation(CampusLocation.Campus.TAGUS, "Pavilhão A");
        foodService = new FoodService("Refeitório 1", location, openingTime, "Comida variada");
        foodService.addAccessRestriction(FoodService.AccessRestriction.STAIRS);
        foodService.getMenu().addMenuItem("Menu A", 2, TypeOfFood.MEAT,true, "This menu is consisted in fries and soup");
        foodService.getMenu().addMenuItem("Menu B", 2, TypeOfFood.FISH,true, "This menu is consisted in a main fish course with fries or soup");
        foodService.getMenu().addMenuItem("Menu C", 2, TypeOfFood.VEGAN,true, "This menu is consisted in a main vegan course with fries and fruit");
        foodService.getMenu().addMenuItem("Menu D", 2, TypeOfFood.VEGETARIAN,true, "This menu is consisted in a vegetarian fish course with fries or soup");
        foodService.getMenu().addMenuItem("Menu E", 2, TypeOfFood.MEAT,true, "This menu is consisted in fries");
        foodServiceList.add(foodService);

        location = new CampusLocation(CampusLocation.Campus.ALAMEDA, "Central");
        for (int i = 2; i < 50; i++) {
            foodServiceList.add(new FoodService("Food Service " + i, location, openingTime, "Something"));
        }
    }
}
