package pt.ulisboa.tecnico.cmov.g16.foodist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.FoodServiceListRecyclerAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodService;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    ArrayList<FoodService> foodServiceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.foodServiceList = new ArrayList<>();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initFoodServiceRecyclerView();
    }

    private void initFoodServiceRecyclerView() {
        rv = findViewById(R.id.food_service_list);
        FoodServiceListRecyclerAdapter adapter = new FoodServiceListRecyclerAdapter(this, foodServiceList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FoodService foodService = new FoodService("Food Service " + 0);
        foodService.addAccessRestriction(FoodService.AccessRestriction.ELEVATOR);
        foodService.addAccessRestriction(FoodService.AccessRestriction.RAMP);
        foodServiceList.add(foodService);

        foodService = new FoodService("Food Service " + 1);
        foodService.addAccessRestriction(FoodService.AccessRestriction.STAIRS);
        foodServiceList.add(foodService);

        for (int i = 2; i < 50; i++) {
            foodServiceList.add(new FoodService("Food Service " + i));
        }
    }
}
