package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.FoodTypeAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodType;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.SaveMenuItemRunnable;

public class NewMenuItemActivity extends AppCompatActivity {

    private static final String TAG = "NewMenuItemActivity";

    Data data;
    Spinner typeOfFood;

    private int foodServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_menu_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        foodServiceId = intent.getIntExtra("foodServiceId", -1);

        data = (Data) getApplicationContext();

        typeOfFood = findViewById(R.id.menu_item_food_type_value);
        setupSpinner();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText title = findViewById(R.id.title);
                EditText price = findViewById(R.id.menu_item_price_value);
                EditText description = findViewById(R.id.description);

                try {
                    MenuItem item = new MenuItem(title.getText().toString(),
                            Double.parseDouble(price.getText().toString()),
                            FoodType.valueOf(typeOfFood.getSelectedItem().toString().toUpperCase()),
                            description.getText().toString());
                    saveMenu(item, foodServiceId);
                }catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "Please setup a valid price", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button cancelButton = findViewById(R.id.cancelButton);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
    }

    private void setupSpinner() {
        final FoodTypeAdapter adapterDietary = new FoodTypeAdapter(this);
        typeOfFood.setAdapter(adapterDietary);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
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

    public void saveMenu(final MenuItem item, final Integer foodServiceId) {
        new GrpcTask(new SaveMenuItemRunnable(item, foodServiceId) {
            @Override
            protected void callback(SaveMenuItemResult result) {
                item.setId(result.getMenuId());
                data.getFoodService(foodServiceId).addMenuItem(item);
                finish();
            }
        }).execute();
    }

}