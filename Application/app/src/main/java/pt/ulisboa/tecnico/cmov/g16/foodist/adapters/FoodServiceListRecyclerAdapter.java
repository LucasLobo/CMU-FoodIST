package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.activities.FoodServiceActivity;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;

public class FoodServiceListRecyclerAdapter extends RecyclerView.Adapter<FoodServiceListRecyclerAdapter.FoodServiceListItemViewHolder>   {

    private static final String TAG = "FoodServiceListRecycler";

    private Context context;
    private ArrayList<FoodService> foodServiceList;

    public FoodServiceListRecyclerAdapter(Context context, ArrayList<FoodService> foodServiceList) {
        this.context = context;
        this.foodServiceList = foodServiceList;
    }

    @NonNull
    @Override
    public FoodServiceListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_service_list_item, parent, false);
        return new FoodServiceListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodServiceListItemViewHolder holder, final int position) {
        final FoodService foodService = foodServiceList.get(position);
        holder.name.setText(foodService.getName());
        holder.location.setText(foodService.getLocatioName());
        holder.queueTime.setText(context.getResources().getString(R.string.time_min, 10));
        holder.walkTime.setText(context.getResources().getString(R.string.time_min, 7));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FoodServiceActivity.class);
                intent.putExtra("index", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodServiceList.size();
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
