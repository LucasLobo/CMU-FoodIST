package pt.ulisboa.tecnico.cmov.g20.foodist.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g20.foodist.model.exceptions.MenuItemMissingId;

public class Menu {

    private HashMap<Integer, MenuItem> menus;

    public Menu(){
        menus = new HashMap<>();
    }

    void addMenuItem(MenuItem item) throws MenuItemMissingId {
        if (item.getId() == -1) throw new MenuItemMissingId();
        menus.put(item.getId(), item);
    }

    public List<MenuItem> getMenuList(){
        return new ArrayList<>(menus.values());
    }

    public MenuItem getMenuItem(Integer id) {
        return menus.get(id);
    }


}
