package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;

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

        populate();
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

    private void populate() {
        setTitle(foodService.getName());

        TextView description = findViewById(R.id.food_service_description);
        description.setText(foodService.getFoodType());

        TextView campus = findViewById(R.id.food_service_campus);
        campus.setText(foodService.getLocation().getCampus().id);

        TextView aproxPrice = findViewById(R.id.food_service_aprox_price_value);
        aproxPrice.setText("5€");

        TextView aproxWaiting = findViewById(R.id.food_service_aprox_waiting_value);
        aproxWaiting.setText("5min");

        TextView location = findViewById(R.id.food_service_location);
        location.setText("Pavilhão de Civil");

        TextView open = findViewById(R.id.food_service_open);
        open.setText("Aberto agora");
        open.setTextColor(Color.rgb(0,100,0));

        TextView openTime = findViewById(R.id.food_service_open_time);
        openTime.setText("Fecha em 30min");
    }

}
