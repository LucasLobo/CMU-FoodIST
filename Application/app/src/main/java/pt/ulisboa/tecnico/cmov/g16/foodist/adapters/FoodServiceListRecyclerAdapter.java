package pt.ulisboa.tecnico.cmov.g16.foodist.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.activities.FoodServiceActivity;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.trajectory.FetchURL;

public class FoodServiceListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "FoodServiceListRecycler";
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private ArrayList<FoodService> foodServiceList;
    private ArrayList<FoodService> filteredFoodServiceList;
    private CampusLocation.Campus currentCampus;
    private User.UserStatus userStatus;
    private EnumSet<FoodType> dietaryConstraints;
    private LatLng userLocation = new LatLng(0,0);
    private HashMap<Integer, String> foodServicesDistanceTime;
    private boolean foodServicesWereFiltered = false;
    private boolean shouldFilterDietaryConstraints = true;
    private boolean newUserLocation = true;

    public FoodServiceListRecyclerAdapter(Context context, ArrayList<FoodService> foodServiceList) {
        this.context = context;
        this.foodServiceList = foodServiceList;
        this.filteredFoodServiceList = foodServiceList;
        foodServicesDistanceTime = new HashMap<>();
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

    public void setUserLocation(LatLng userLocation){
        if(!this.userLocation.equals(userLocation)) {
            this.userLocation = userLocation;
            newUserLocation = true;
        }
    }

    public void toggleConstrainsFilter() {
        shouldFilterDietaryConstraints = !shouldFilterDietaryConstraints;
        updateList();
    }

    public void updateList() {
        foodServicesWereFiltered = false;
        ArrayList<FoodService> filteredList = new ArrayList<>();
        for (FoodService foodService : foodServiceList) {
            if (!foodService.getCampus().equals(currentCampus)) {
                continue;
            }
            if (!foodService.isOpen(userStatus)) {
                continue;
            }

            if (shouldFilterDietaryConstraints && !foodService.meetsConstraints(dietaryConstraints, true)) {
                foodServicesWereFiltered = true;
                continue;
            }

            filteredList.add(foodService);
            if(newUserLocation)
                foodServicesDistanceTime.clear();
            if(!foodServicesDistanceTime.containsKey(foodService.getId()))
                calculateDistanceTime(foodService);
        }
        filteredFoodServiceList = filteredList;
        newUserLocation = false;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.food_service_list_header, parent, false);
            return new FoodServiceListHeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.food_service_list_item, parent, false);
            return new FoodServiceListItemViewHolder(view);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof FoodServiceListHeaderViewHolder) {
            FoodServiceListHeaderViewHolder castHolder = (FoodServiceListHeaderViewHolder) holder;
            if (shouldFilterDietaryConstraints) {
                castHolder.notice.setText(R.string.results_filtered);
            } else {
                castHolder.notice.setText(R.string.no_dietary_constraints);
                castHolder.notice.setTextColor(context.getResources().getColor(R.color.colorAccent));
            }


        } else if (holder instanceof FoodServiceListItemViewHolder) {
            FoodServiceListItemViewHolder castHolder = (FoodServiceListItemViewHolder) holder;
            final FoodService foodService = filteredFoodServiceList.get(getPosition(position));
            castHolder.name.setText(foodService.getName());
            castHolder.queueTime.setText(context.getResources().getString(R.string.time_min, 10));
            castHolder.walkTime.setText(displayDistanceTime(foodServicesDistanceTime.get(foodService.getId())));
            castHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FoodServiceActivity.class);
                    intent.putExtra("id", foodService.getId());
                    intent.putExtra("distanceTime", foodServicesDistanceTime.get(foodService.getId()));
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (showNotice()) return filteredFoodServiceList.size() + 1;
        return filteredFoodServiceList.size();
    }
    public HashMap<Integer,String> getFoodServicesDistanceTime(){
        return foodServicesDistanceTime;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0 && showNotice();
    }

    private int getPosition(int position) {
        if (showNotice()) return position - 1;
        return position;
    }

    private boolean showNotice() {
        return !shouldFilterDietaryConstraints || foodServicesWereFiltered;
    }

    private void calculateDistanceTime(FoodService foodService){
        FetchURL fetchURL = new FetchURL(context);
        String url = fetchURL.getDirectionsUrl(userLocation,
                new LatLng(foodService.getLocation().getLatitude(), foodService.getLocation().getLongitude()));
        fetchURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, "false", Integer.toString(foodService.getId()));
    }

    private String displayDistanceTime(Object object){
        if(object == null)
            return "Calculating...";
        return object.toString();
    }

    static class FoodServiceListItemViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView queueTime;
        TextView walkTime;
        CardView parentLayout;

        FoodServiceListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.food_service_list_item_name);
            queueTime = itemView.findViewById(R.id.food_service_list_item_queue_time);
            walkTime = itemView.findViewById(R.id.food_service_list_item_walking_time);
            parentLayout = itemView.findViewById(R.id.food_service_list_item_parent_layout);
        }
    }

    static class FoodServiceListHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView notice;
        FoodServiceListHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            notice = itemView.findViewById(R.id.food_service_list_header);
        }
    }

}
