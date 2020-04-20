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
    Menu menu;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        initData();
        instantiateMenu();
    }

    private void instantiateMenu(){
        this.menu = new Menu();
        menu.addMenuItem("Menu A", 2, TypeOfFood.MEAT,true, "dkajsdkajdaksdjladkas");
        menu.addMenuItem("Menu B", 2, TypeOfFood.FISH,true, "knmsokmdaosdjasdia");
        menu.addMenuItem("Menu C", 2, TypeOfFood.VEGETARIAN,true, "abcd");
        menu.addMenuItem("Menu D", 2, TypeOfFood.VEGAN,true, "qiwoehioqhe");

    }

    public Menu getMenu() {
        return menu;
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

        CampusLocation location = new CampusLocation(CampusLocation.Campus.ALAMEDA);
        FoodService foodService = new FoodService("Bar 0", location, openingTime, "Sanduiches");
        foodService.addAccessRestriction(FoodService.AccessRestriction.ELEVATOR);
        foodService.addAccessRestriction(FoodService.AccessRestriction.RAMP);
        foodServiceList.add(foodService);

        location = new CampusLocation(CampusLocation.Campus.TAGUS);
        foodService = new FoodService("Refeitório 1", location, openingTime, "Comida variada");
        foodService.addAccessRestriction(FoodService.AccessRestriction.STAIRS);
        foodServiceList.add(foodService);

        location = new CampusLocation(CampusLocation.Campus.ALAMEDA);
        for (int i = 2; i < 50; i++) {
            foodServiceList.add(new FoodService("Food Service " + i, location, openingTime, "Something"));
        }
    }
}
