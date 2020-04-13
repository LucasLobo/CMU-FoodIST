package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.MenuItemAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.TypeOfFood;

public class MainActivity extends AppCompatActivity {

    private Menu menu;
    Data data;
    ListView listView;
    MenuItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        data = (Data) getApplicationContext();
        menu = data.getMenu();

        adapter = new MenuItemAdapter(this, menu.getMenuList());
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);



        Button buttonNewMenuItem = findViewById(R.id.buttonNewMenuItem);
        buttonNewMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewMenuItemActivity.class);
                startActivity(intent);
                adapter.getFilter().filter(" ");
            }
        });


        ToggleButton meatButton = findViewById(R.id.buttonMeat);
        meatButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adapter.toggleMeat();
                adapter.getFilter().filter(" ");
            }
        });

        ToggleButton fishButton = findViewById(R.id.buttonFish);
        fishButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adapter.toggleFish();
                adapter.getFilter().filter(" ");
            }
        });

        ToggleButton veganButton = findViewById(R.id.buttonVegan);
        veganButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adapter.toggleVegan();
                adapter.getFilter().filter(" ");
            }
        });

        ToggleButton vegetarianButton = findViewById(R.id.buttonVegatarian);
        vegetarianButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adapter.toggleVegetarian();
                adapter.getFilter().filter(" ");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItem item = adapter.getItem(i);
                Intent intent = new Intent(MainActivity.this, MenuItemActivity.class);
                intent.putExtra("index", menu.getMenuList().indexOf(item));
                startActivity(intent);

            }
        });

        adapter.getFilter().filter(" ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.getFilter().filter(" ");
    }
}
