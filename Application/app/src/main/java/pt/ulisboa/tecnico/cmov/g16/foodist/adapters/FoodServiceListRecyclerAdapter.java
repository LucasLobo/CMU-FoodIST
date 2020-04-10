package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

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

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
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
        holder.text.setText(foodServiceList.get(position).getName());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<FoodService.AccessRestriction> accessRestrictions = foodServiceList.get(position).getAccessRestrictions();

                StringBuilder restrictions = new StringBuilder();
                restrictions.append(" Restrictions: ");

                for (FoodService.AccessRestriction restriction : accessRestrictions) {
                    restrictions.append(context.getString(restriction.resourceId));
                    restrictions.append(",");
                }

                Snackbar.make(v,
                        foodServiceList.get(position).getName() + restrictions.toString(),
                        Snackbar.LENGTH_LONG)
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
