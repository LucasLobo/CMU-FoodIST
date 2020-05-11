package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.widget.ImageView;

import java.util.Date;
import java.util.LinkedList;

public class Menu {

    private Date lastUpdated;
    private LinkedList<MenuItem> menuList;

    public Menu(){
        menuList = new LinkedList<>();
        lastUpdated = new Date();
    }


    public String getLastUpdated() {
        return lastUpdated.toString();
    }

    void addMenuItem(MenuItem item) {
        menuList.add(item);
    }

    public void addMenuItem(String name, double price, FoodType foodType, String description) {
        MenuItem item = new MenuItem(name, price, foodType, description, new LinkedList<ImageView>());
        menuList.add(item);
    }

    public void removeMenuItem(){
        //TO-DO
    }

    // * SETTERS * //

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // * GETTERS * //

    public LinkedList<MenuItem> getMenuList(){
        return menuList;
    }

    public LinkedList<String> getMenuListString(){
        LinkedList<String> list = new LinkedList<>();
        for(int i = 0; i!=menuList.size(); i++){
            list.add(menuList.get(i).toString());
        }
        return list;
    }


}
