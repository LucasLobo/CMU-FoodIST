package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.threeten.bp.LocalTime;

import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.OpeningTime;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class FoodServiceActivity extends AppCompatActivity {

    Data data;
    FoodService foodService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_service);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();
        Intent intent = getIntent();
        int index = intent.getIntExtra("index", -1);

        if (index == -1) {
            finish();
        }

        foodService = data.getFoodService(index);
        populate(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void populate(final int index) {
        setTitle(foodService.getName());

        TextView name = findViewById(R.id.food_service_name);
        name.setText(foodService.getName());

        TextView description = findViewById(R.id.food_service_description);
        description.setText(foodService.getFoodType());



        TextView aproxPrice = findViewById(R.id.food_service_aprox_price);
        aproxPrice.setText(getString(R.string.menu_price, 5));


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

        OpeningTime.Schedule schedule = foodService.getOpeningTime().getSchedule(user.getStatus());
        LocalTime openTime = schedule.getOpenTime();
        LocalTime closeTime = schedule.getCloseTime();

        TextView openingTime = findViewById(R.id.food_service_opening_time);
        openingTime.setText(getString(R.string.opening_time, openTime.getHour(), openTime.getMinute(), closeTime.getHour(), closeTime.getMinute()));
        TextView aproxWaiting = findViewById(R.id.food_service_aprox_waiting);
        aproxWaiting.setText(getString(R.string.waiting_time,5));


        TextView constraintsNotice = findViewById(R.id.food_service_food_constraints_notice);
        if (!user.shouldApplyConstraintsFilter() || foodService.meetsConstraints(user.getDietaryConstraints(), false)) {
            constraintsNotice.setVisibility(View.GONE);
        }



        TextView campus = findViewById(R.id.food_service_campus);
        campus.setText(foodService.getLocation().getCampus().id);

        TextView location = findViewById(R.id.food_service_location);
        location.setText(foodService.getLocationName());

        TextView walkingTime = findViewById(R.id.food_service_walking_time);
        walkingTime.setText(getString(R.string.walking_time, 5));

        LinearLayout menuList = findViewById(R.id.food_service_menu);
        menuList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodServiceActivity.this, MenuActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        });


    }

}
