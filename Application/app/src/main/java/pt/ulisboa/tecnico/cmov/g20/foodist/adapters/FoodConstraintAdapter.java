package pt.ulisboa.tecnico.cmov.g20.foodist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import pt.ulisboa.tecnico.cmov.g20.foodist.R;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.User;

public class FoodConstraintAdapter extends RecyclerView.Adapter<FoodConstraintAdapter.ViewHolder> {

    private FoodType[] foodType = FoodType.values();
    private User user;

    public FoodConstraintAdapter(User user){
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_type_constraint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.checkBox.setText(foodType[position].resourceId);
        if (user.getDietaryConstraints().contains(foodType[position])) {
            holder.checkBox.setChecked(true);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!user.getDietaryConstraints().contains(foodType[position])) {
                    user.addDietaryConstraints(foodType[position]);

                } else {
                    user.removeDietaryConstraints(foodType[position]);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return foodType.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        final View view;
        CheckBox checkBox;

        ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            this.checkBox = view.findViewById(R.id.checkBox);
        }
    }
}
