package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.app.Application;

public class Data extends Application {

    Menu menu;

    public void onCreate(){
        super.onCreate();
        instantiateMenu();
    }

    private void instantiateMenu(){
        this.menu = new Menu();
        menu.addMenuItem("Menu A", 2, TypeOfFood.MEAT,true, "dkajsdkajdaksdjladkas", FoodCourse.MAIN);
        menu.addMenuItem("Menu B", 2, TypeOfFood.FISH,true, "knmsokmdaosdjasdia", FoodCourse.MAIN);
        menu.addMenuItem("Menu C", 2, TypeOfFood.VEGETARIAN,true, "abcd", FoodCourse.MAIN);
        menu.addMenuItem("Menu D", 2, TypeOfFood.VEGAN,true, "qiwoehioqhe", FoodCourse.MAIN);

    }

    public Menu getMenu() {
        return menu;
    }
}
