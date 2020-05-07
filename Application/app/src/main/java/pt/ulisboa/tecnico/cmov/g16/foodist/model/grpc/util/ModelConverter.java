package pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util;

import android.widget.ImageView;

import com.grpc.Contract;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class ModelConverter {

    public static User.UserStatus StringToUserStatus(String userStatusString) {
        EnumSet<User.UserStatus> userStatuses = EnumSet.allOf(User.UserStatus.class);

        for (User.UserStatus status : userStatuses) {
            if (userStatusString.equals(status.name())) return status;
        }

        return null;
    }

    public static FoodType StringToFoodType(String foodTypeString) {
        EnumSet<FoodType> foodTypes = EnumSet.allOf(FoodType.class);

        for (FoodType foodType : foodTypes) {
            if (foodTypeString.equals(foodType.name())) return foodType;
        }

        return null;
    }

    public static EnumSet<FoodType> StringArrayToFoodTypeSet(List<String> foodTypeArray) {
        EnumSet<FoodType> foodTypes = EnumSet.noneOf(FoodType.class);

        for (String foodTypeString : foodTypeArray) {
            foodTypes.add(StringToFoodType(foodTypeString));
        }

        return foodTypes;
    }

    public static List<String> FoodTypeSetToList(EnumSet<FoodType> foodTypesSet) {
        List<String> foodTypes = new ArrayList<>();

        for (FoodType foodType : foodTypesSet) {
            foodTypes.add(foodType.name());
        }
        return foodTypes;
    }

    public static MenuItem ContractMenuItemToMenuItem(Contract.MenuItem menuItem) {
        return new MenuItem(menuItem.getName(),menuItem.getPrice(), StringToFoodType(menuItem.getFoodType()), true, menuItem.getDescription(), 0, new LinkedList<ImageView>());
    }

    public static List<MenuItem> ContractMenuItemsToMenuItems(List<Contract.MenuItem> contractMenuItems) {
        List<MenuItem> menuItems = new ArrayList<>();
        if (contractMenuItems != null) {
            for (Contract.MenuItem contractMenuItem : contractMenuItems) {
                menuItems.add(ContractMenuItemToMenuItem(contractMenuItem));
            }
        }
        return menuItems;
    }
}
