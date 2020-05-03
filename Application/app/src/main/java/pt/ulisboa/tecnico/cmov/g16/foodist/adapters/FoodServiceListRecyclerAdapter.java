package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.activities.FoodServiceActivity;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class FoodServiceListRecyclerAdapter extends RecyclerView.Adapter<FoodServiceListRecyclerAdapter.FoodServiceListItemViewHolder>   {

    private static final String TAG = "FoodServiceListRecycler";

    private Context context;
    private ArrayList<FoodService> foodServiceList;
    private ArrayList<FoodService> filteredFoodServiceList;
    private CampusLocation.Campus currentCampus;
    private User.UserStatus userStatus;
    private EnumSet<FoodType> dietaryConstraints;

    public FoodServiceListRecyclerAdapter(Context context, ArrayList<FoodService> foodServiceList) {
        this.context = context;
        this.foodServiceList = foodServiceList;
        this.filteredFoodServiceList = foodServiceList;
    }

    public void setCampus(CampusLocation.Campus campus) {
        currentCampus = campus;
    }

    public void setUserStatus(User.UserStatus status) {
        userStatus = status;
    }

    public void setDietaryConstraints(EnumSet<FoodType> set) {
        dietaryConstraints = set;
    }

    public void updateList() {
        ArrayList<FoodService> filteredList = new ArrayList<>();
        for (FoodService foodService : foodServiceList) {
            if (!foodService.getCampus().equals(currentCampus)) {
                continue;
            }
            if (!foodService.isOpen(userStatus)) {
                continue;
            }

            EnumSet<FoodType> foodServiceFoodTypes = EnumSet.copyOf(foodService.getFoodTypes());

            if (!foodServiceFoodTypes.isEmpty()) {
                foodServiceFoodTypes.removeAll(dietaryConstraints);
                if (foodServiceFoodTypes.isEmpty()) {
                    continue;
                }
            }

            filteredList.add(foodService);
        }
        filteredFoodServiceList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodServiceListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_service_list_item, parent, false);
        return new FoodServiceListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodServiceListItemViewHolder holder, final int position) {
        final FoodService foodService = filteredFoodServiceList.get(position);
        holder.name.setText(foodService.getName());
        holder.location.setText(foodService.getLocationName());
        holder.queueTime.setText(context.getResources().getString(R.string.time_min, 10));
        holder.walkTime.setText(context.getResources().getString(R.string.time_min, 7));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FoodServiceActivity.class);
                intent.putExtra("index", foodServiceList.indexOf(foodService));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredFoodServiceList.size();
    }

    static class FoodServiceListItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView location;
        TextView queueTime;
        TextView walkTime;
        CardView parentLayout;

        FoodServiceListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.food_service_list_item_name);
            location = itemView.findViewById(R.id.food_service_list_item_location);
            queueTime = itemView.findViewById(R.id.food_service_list_item_queue_time);
            walkTime = itemView.findViewById(R.id.food_service_list_item_walking_time);
            parentLayout = itemView.findViewById(R.id.food_service_list_item_parent_layout);
        }
    }
}
