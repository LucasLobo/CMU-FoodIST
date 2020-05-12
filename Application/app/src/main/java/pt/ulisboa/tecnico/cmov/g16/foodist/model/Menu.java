package pt.ulisboa.tecnico.cmov.g16.foodist.model;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.exceptions.MenuItemMissingId;

public class Menu {

    private Date lastUpdated;
    private HashMap<Integer, MenuItem> menus;

    public Menu(){
        menus = new HashMap<>();
        lastUpdated = new Date();
    }

    public String getLastUpdated() {
        return lastUpdated.toString();
    }

    void addMenuItem(MenuItem item) throws MenuItemMissingId {
        if (item.getId() == -1) throw new MenuItemMissingId();
        menus.put(item.getId(), item);
    }

    public void removeMenuItem(){
        //TO-DO
    }

    // * SETTERS * //

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // * GETTERS * //

    public List<MenuItem> getMenuList(){
        return new ArrayList<>(menus.values());
    }

    public MenuItem getMenuItem(Integer id) {
        return menus.get(id);
    }


}
