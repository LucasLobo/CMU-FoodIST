package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
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
        holder.text.setText(foodService.getName());

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
        TextView text;
        RelativeLayout parentLayout;

        FoodServiceListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.food_service_list_item_text);
            parentLayout = itemView.findViewById(R.id.food_service_list_item_parent_layout);
        }
    }
}
