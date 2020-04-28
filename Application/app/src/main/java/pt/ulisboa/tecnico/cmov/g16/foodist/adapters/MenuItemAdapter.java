package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.TypeOfFood;

public class MenuItemAdapter extends ArrayAdapter<MenuItem> implements Filterable {

    private Context mContext;
    private LinkedList<MenuItem> originalList;
    private LinkedList<MenuItem> filteredList;
    private MenuFilter mFilter = new MenuFilter();

    // FILTER OPTIONS //
    private boolean meat;
    private boolean fish;
    private boolean vegan;
    private boolean vegetarian;


    public MenuItemAdapter(Context mContext, LinkedList<MenuItem> list){
        super(mContext, 0, list);
        this.mContext = mContext;
        this.originalList = list;
        this.filteredList = list;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.menu_item, parent, false);

        MenuItem item = filteredList.get(position);

        TextView title = listItem.findViewById(R.id.title);
        title.setText(item.getName());

        TextView foodType = listItem.findViewById(R.id.foodType);
        foodType.setText(item.getFoodType().resourceId);
        ImageView image = listItem.findViewById(R.id.foodImage);
        if(item.getImages().size()>0)
            image.setImageDrawable(item.getImages().getLast().getDrawable());
        else
            image.setImageResource(android.R.drawable.ic_delete);

        return listItem;

    }

    public MenuItem getItem(int i ){
        return filteredList.get(i);
    }
    public int getCount(){
        return filteredList.size();
    }

    private Boolean filterFoodType(MenuItem item){
        if(meat && item.getFoodType().equals(TypeOfFood.MEAT))
            return true;
        else if(fish && item.getFoodType().equals(TypeOfFood.FISH))
            return true;
        else if(vegan && item.getFoodType().equals(TypeOfFood.VEGAN))
            return true;
        else if(vegetarian && item.getFoodType().equals(TypeOfFood.VEGETARIAN))
            return true;
        return false;
    }

    public void toggleMeat(){
        meat = !meat;
    }
    public void toggleFish(){
        fish = !fish;
    }
    public void toggleVegan(){
        vegan = !vegan;
    }
    public void toggleVegetarian(){
        vegetarian = !vegetarian;
    }

    public boolean isMeat() {
        return meat;
    }
    public boolean isFish() {
        return fish;
    }
    public boolean isVegan() {
        return vegan;
    }
    public boolean isVegetarian() {
        return vegetarian;
    }

    public Filter getFilter(){
        return mFilter;
    }

    public class MenuFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults results = new FilterResults();
            final LinkedList<MenuItem> list = originalList;
            int count = list.size();
            final LinkedList<MenuItem> nList = new LinkedList<>();

            for(int i = 0; i!=count; i++){
                MenuItem item = list.get(i);
                if(filterFoodType(item))
                    nList.add(item);
            }

            results.values = nList;
            results.count = nList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredList = (LinkedList<MenuItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
