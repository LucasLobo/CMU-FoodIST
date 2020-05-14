package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.MenuItemAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.OpeningTime;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class FoodServiceActivity extends AppCompatActivity {

    Data data;
    FoodService foodService;
    GoogleMap mMap;
    String distanceTime;
    MenuItemAdapter adapter;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        distanceTime = intent.getStringExtra("distanceTime");
        if (id == -1) {
            finish();
        }

        foodService = data.getFoodService(id);
        user = data.getUser();
        populate(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Let's eat at " + foodService.getName() + "!"
                        + "\nCampus: " + getString(foodService.getCampus().id)
                        + "\nAvailable Food Types: " + getFoodTypes());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "Share:");
                startActivity(shareIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_food_service_share, menu);
        return true;
    }

    private void populate(final int id) {
        setTitle(foodService.getName());

        TextView name = findViewById(R.id.food_service_name);
        name.setText(foodService.getName());

        TextView open = findViewById(R.id.food_service_open);

        User user = data.getUser();
        Boolean isOpen = foodService.isOpen(user.getStatus());

        if (isOpen) {
            open.setText(R.string.open);
            open.setTextColor(Color.rgb(0,100,0));


        } else {
            open.setText(R.string.closed);
            open.setTextColor(Color.rgb(200,0,0));
        }

        ArrayList<OpeningTime.Schedule> scheduleList = foodService.getOpeningTime().getScheduleList(user.getStatus());
        TextView openingTime = findViewById(R.id.food_service_opening_time);

        StringBuilder scheduleString = new StringBuilder();
        for (OpeningTime.Schedule schedule : scheduleList) {
            LocalTime openTime = schedule.getOpenTime();
            LocalTime closeTime = schedule.getCloseTime();
            if (scheduleString.length() > 0) scheduleString.append('\n');
            scheduleString.append(getString(R.string.opening_time, openTime.getHour(), openTime.getMinute(), closeTime.getHour(), closeTime.getMinute()));
        }
        openingTime.setText(scheduleString.toString());

        TextView aproxWaiting = findViewById(R.id.food_service_aprox_waiting);

        if (foodService.getQueueTime() == -2) {
            aproxWaiting.setText(R.string.calculating);
        } else if (foodService.getQueueTime() == -1) {
            aproxWaiting.setText(getString(R.string.waiting_time, 0));
        } else if (foodService.getQueueTime() >= 0) {
            aproxWaiting.setText(getString(R.string.waiting_time, foodService.getQueueTime()));
        }


        TextView constraintsNotice = findViewById(R.id.food_service_food_constraints_notice);
        if (!user.shouldApplyConstraintsFilter() || foodService.meetsConstraints(user.getDietaryConstraints(), false)) {
            constraintsNotice.setVisibility(View.GONE);
        }

        TextView campus = findViewById(R.id.food_service_campus);
        campus.setText(foodService.getLocation().getCampus().id);

        TextView walkingTime = findViewById(R.id.food_service_walking_time);
        walkingTime.setText(getString(R.string.walking_time, distanceTime));

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng destination = new LatLng(foodService.getLocation().getLatitude(), foodService.getLocation().getLongitude());
                mMap.addMarker(new MarkerOptions().position(destination).title(foodService.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
                mMap.setMinZoomPreference(15);
                mMap.setMaxZoomPreference(25);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Intent intent = new Intent(FoodServiceActivity.this, MapActivity.class);
                        intent.putExtra("Latitude", foodService.getLocation().getLatitude());
                        intent.putExtra("Longitude", foodService.getLocation().getLongitude());
                        intent.putExtra("foodServiceName", foodService.getName());
                        startActivity(intent);
                    }
                });
                mMap.getUiSettings().setAllGesturesEnabled(false);
            }
        });

        Button buttonNewMenuItem = findViewById(R.id.buttonNewMenuItem);
        buttonNewMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodServiceActivity.this, NewMenuItemActivity.class);
                intent.putExtra("foodServiceId", foodService.getId());
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.menu_list);
        adapter = new MenuItemAdapter(this, foodService.getMenu().getMenuList(), foodService.getId());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private String getFoodTypes() {
        StringBuilder foodTypes = new StringBuilder();
        String separator = "";
        for (FoodType foodType : foodService.getFoodTypes()) {
            foodTypes.append(separator).append(getString(foodType.resourceId));
            separator = ", ";
        }
        return foodTypes.toString();
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter.setList(foodService.getMenu().getMenuList());
        if (user.shouldApplyConstraintsFilter()) {
            adapter.setDietaryConstraints(user.getDietaryConstraints());
        } else {
            adapter.setDietaryConstraints(EnumSet.noneOf(FoodType.class));
        }
    }
}
