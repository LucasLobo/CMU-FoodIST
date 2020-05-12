package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;

public class MenuItemAdapter extends ArrayAdapter<MenuItem> implements Filterable {

    private Context mContext;
    private List<MenuItem> originalList;
    private List<MenuItem> filteredList;

    private EnumSet<FoodType> dietaryConstraints;

    public MenuItemAdapter(Context mContext, List<MenuItem> list){
        super(mContext, 0, list);
        this.mContext = mContext;
        this.originalList = list;
        this.filteredList = list;
    }

    public void setList(List<MenuItem> list) {
        originalList = list;
        notifyDataSetChanged();
    }

    public void setDietaryConstraints(EnumSet<FoodType> set) {
        dietaryConstraints = set;
        updateList();
    }

    private void updateList() {
        filteredList = new LinkedList<>();
        for (MenuItem item : originalList) {
            if (!dietaryConstraints.contains(item.getFoodType())) {
                filteredList.add(item);
            }
        }
        notifyDataSetChanged();
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.menu_item, parent, false);

        }

        MenuItem item = filteredList.get(position);

        TextView title = listItem.findViewById(R.id.title);
        title.setText(item.getName());

        TextView foodType = listItem.findViewById(R.id.foodType);
        foodType.setText(item.getFoodType().resourceId);
        ImageView image = listItem.findViewById(R.id.foodImage);

        ArrayList<Bitmap> images = item.getImages();
        if (images.size() > 0) {
            image.setImageBitmap(images.get(images.size()-1));
        }
        else {
            image.setImageResource(android.R.drawable.ic_delete);
        }

        return listItem;
    }

    public MenuItem getItem(int i ){
        return filteredList.get(i);
    }
    public int getCount(){
        return filteredList.size();
    }

}
