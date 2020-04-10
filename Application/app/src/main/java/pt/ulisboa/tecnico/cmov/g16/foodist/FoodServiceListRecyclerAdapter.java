package pt.ulisboa.tecnico.cmov.g16.foodist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FoodServiceListRecyclerAdapter extends RecyclerView.Adapter<FoodServiceListRecyclerAdapter.FoodServiceListItemViewHolder>   {

    private static final String TAG = "FoodServiceListRecycler";

    private Context context;
    private ArrayList<String> foodServiceList;

    FoodServiceListRecyclerAdapter(Context context, ArrayList<String> foodServiceList) {
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
        holder.text.setText(foodServiceList.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Should open: " + foodServiceList.get(position), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
