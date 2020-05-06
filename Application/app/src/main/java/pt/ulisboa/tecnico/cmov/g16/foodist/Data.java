package pt.ulisboa.tecnico.cmov.g16.foodist;

import android.app.Application;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class Data extends Application {

    private static final String TAG = "Data";

    HashMap<Integer, FoodService> foodServiceHashMap;
    User user;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        initData();
    }

    public ArrayList<FoodService> getFoodServiceList() {
        return new ArrayList<>(foodServiceHashMap.values());
    }

    public FoodService getFoodService(Integer id) {
        return foodServiceHashMap.get(id);
    }

    public User getUser() {
        return user;
    }

    public String serialize(Object item){
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(item);
            so.flush();
            return bo.toString();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public Object deserialize(String obj){
        String serializedObject = "";
        try {
            byte objBytes[] = serializedObject.getBytes();
            ByteArrayInputStream bi = new ByteArrayInputStream(objBytes);
            ObjectInputStream si = new ObjectInputStream(bi);
            return si.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    private void initData() {
        initUser();
        initFoodServices();
    }

    private void initUser() {
        this.user = new User();
    }

    private void initFoodServices() {
        this.foodServiceHashMap = new HashMap<>();

        int id = 0;

        FoodService fakePlace = new FoodService(id++, "Fake Place", 38.7352722896, -9.13268566132);
        fakePlace.addSchedule(0,0,23,59);
        fakePlace.addAccessRestriction(FoodService.AccessRestriction.WHEEL_CHAIR);
        fakePlace.addAccessRestriction(FoodService.AccessRestriction.STAIRS);
        fakePlace.addAccessRestriction(FoodService.AccessRestriction.ELEVATOR);
        fakePlace.addAccessRestriction(FoodService.AccessRestriction.RAMP);
        fakePlace.addMenuItem("Meat menu", 5, FoodType.MEAT, true, "Menu with meat");
        fakePlace.addMenuItem("Fish menu", 5, FoodType.FISH, true, "Menu with fish");
        fakePlace.addMenuItem("Vegan menu", 5, FoodType.VEGAN, true, "Vegan menu");
        fakePlace.addMenuItem("Vegetarian menu", 5, FoodType.VEGETARIAN, true, "Vegetarian menu");
        foodServiceHashMap.put(fakePlace.getId(), fakePlace);

        FoodService centralBar = new FoodService(id++, "Central Bar", 38.736606, -9.139532);
        centralBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(centralBar.getId(), centralBar);

        FoodService civilBar = new FoodService(id++, "Civil Bar", 38.736988, -9.139955);
        civilBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(civilBar.getId(), civilBar);

        FoodService civilCafeteria = new FoodService(id++, "Civil Cafeteria", 38.737650, -9.140384);
        civilCafeteria.addSchedule(12,0,15,0);
        foodServiceHashMap.put(civilCafeteria.getId(), civilCafeteria);

        FoodService senaPastryShop = new FoodService(id++, "Sena Pastry Shop", 38.737677, -9.138672);
        senaPastryShop.addSchedule(8,0,19,0);
        foodServiceHashMap.put(senaPastryShop.getId(), senaPastryShop);

        FoodService mechyBar = new FoodService(id++, "Mechy Bar", 38.737247, -9.137434);
        mechyBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(mechyBar.getId(), mechyBar);

        FoodService aeistBar = new FoodService(id++, "AEIST Bar", 38.736542, -9.137226);
        aeistBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(aeistBar.getId(), aeistBar);

        FoodService aeistEsplanade = new FoodService(id++, "AEIST Esplanade", 38.736318, -9.137820);
        aeistEsplanade.addSchedule(9,0,17,0);
        foodServiceHashMap.put(aeistEsplanade.getId(), aeistEsplanade);

        FoodService chemyBar = new FoodService(id++, "Chemy Bar", 38.736240, -9.138302);
        chemyBar.addSchedule(9,0,17,0);
        foodServiceHashMap.put(chemyBar.getId(), chemyBar);

        FoodService sasCafeteria = new FoodService(id++, "SAS Cafeteria", 38.736571, -9.137036);
        sasCafeteria.addSchedule(9,0,21,0);
        foodServiceHashMap.put(sasCafeteria.getId(), sasCafeteria);

        FoodService mathCafeteria = new FoodService(id++, "Math Cafeteria", 38.735508, -9.139645);
        mathCafeteria.addSchedule(User.UserStatus.STUDENT,13,30,15,0);
        mathCafeteria.addSchedule(User.UserStatus.GENERAL_PUBLIC, 13,30,15,0);
        mathCafeteria.addSchedule(User.UserStatus.PROFESSOR,12,0,15,0);
        mathCafeteria.addSchedule(User.UserStatus.RESEARCHER,12,0,15,0);
        mathCafeteria.addSchedule(User.UserStatus.STAFF,12,0,15,0);
        foodServiceHashMap.put(mathCafeteria.getId(), mathCafeteria);

        FoodService complexBar = new FoodService(id++, "Complex Bar", 38.736050, -9.140156);
        complexBar.addSchedule(User.UserStatus.STUDENT,9,0,12,0);
        complexBar.addSchedule(User.UserStatus.STUDENT,14,0,17,0);
        complexBar.addSchedule(User.UserStatus.GENERAL_PUBLIC,9,0,12,0);
        complexBar.addSchedule(User.UserStatus.GENERAL_PUBLIC,14,0,17,0);
        complexBar.addSchedule(User.UserStatus.PROFESSOR,9,0,17,0);
        complexBar.addSchedule(User.UserStatus.RESEARCHER,9,0,17,0);
        complexBar.addSchedule(User.UserStatus.STAFF,9,0,17,0);
        foodServiceHashMap.put(complexBar.getId(), complexBar);

        FoodService tagusCafeteria = new FoodService(id++, "Tagus Cafeteria", 38.737802, -9.303223);
        tagusCafeteria.addSchedule(12,0,15,0);
        foodServiceHashMap.put(tagusCafeteria.getId(), tagusCafeteria);

        FoodService redBar = new FoodService(id++, "Red Bar", 38.736546, -9.302207);
        redBar.addSchedule(8,0,22,0);
        foodServiceHashMap.put(redBar.getId(), redBar);

        FoodService greenBar = new FoodService(id++, "Green Bar", 38.738004, -9.303058);
        greenBar.addSchedule(7,0,19,0);
        foodServiceHashMap.put(greenBar.getId(), greenBar);

        FoodService ctnCafeteria = new FoodService(id++, "CTN Cafeteria", 38.812522, -9.093773);
        ctnCafeteria.addSchedule(12,0,14,0);
        foodServiceHashMap.put(ctnCafeteria.getId(), ctnCafeteria);

        FoodService ctnBar = new FoodService(id++, "CTN Bar", 38.812522, -9.093773);
        ctnBar.addSchedule(8,30,12,0);
        ctnBar.addSchedule(15,30,16,30);
        foodServiceHashMap.put(ctnBar.getId(), ctnBar);

        initMenus();
    }

    private void initMenus() {

    }
}
