package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.TypeOfFood;

public class NewMenuItemActivity extends AppCompatActivity {

    Data data;
    Spinner typeOfFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_menu_item);
        data = (Data) getApplicationContext();

        typeOfFood = findViewById(R.id.foodType);
        setupSpinner();


        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText title = findViewById(R.id.title);
                EditText price = findViewById(R.id.price);
                EditText description = findViewById(R.id.description);
                EditText discount = findViewById(R.id.discount);

                MenuItem item = new MenuItem(title.getText().toString(), Double.parseDouble(price.getText().toString()), TypeOfFood.valueOf(typeOfFood.getSelectedItem().toString()), true, description.getText().toString());
                item.setDiscount(Integer.parseInt(discount.getText().toString()));
                data.getMenu().getMenuList().add(item);
                finish();
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.food_type_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfFood.setAdapter(adapter);
    }

}
