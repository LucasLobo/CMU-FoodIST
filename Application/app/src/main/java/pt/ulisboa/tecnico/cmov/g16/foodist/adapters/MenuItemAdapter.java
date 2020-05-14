package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.activities.MenuItemActivity;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    private Context context;
    private Data data;
    private Integer foodServiceId;
    private List<MenuItem> originalList;
    private List<MenuItem> filteredList;

    private EnumSet<FoodType> dietaryConstraints;

    public MenuItemAdapter(Context context, List<MenuItem> list, Integer foodServiceId){
        this.context = context;
        data = (Data) context.getApplicationContext();
        this.originalList = list;
        this.filteredList = list;
        this.foodServiceId = foodServiceId;
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

    private MenuItem getItem(int i){
        return filteredList.get(i);
    }


    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_recyclerview_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuItemViewHolder holder, int position) {
        final MenuItem menuItem = getItem(position);
        holder.title.setText(menuItem.getName());
        holder.foodType.setText(menuItem.getFoodType().resourceId);

        Integer randomImageId = menuItem.getRandomImageId();
        if (randomImageId != -1) {
            Bitmap bitmap = data.getImage(randomImageId);
            holder.image.setImageBitmap(bitmap);
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MenuItemActivity.class);
                intent.putExtra("foodServiceId", foodServiceId);
                intent.putExtra("menuItemId", menuItem.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        CardView parentLayout;
        TextView title;
        TextView foodType;
        ImageView image;

        MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.menu_recyclerview_item_card_view);
            title = itemView.findViewById(R.id.menu_recyclerview_item_name);
            foodType = itemView.findViewById(R.id.menu_recyclerview_item_food_type);
            image = itemView.findViewById(R.id.menu_recyclerview_item_image);
        }
    }
}
