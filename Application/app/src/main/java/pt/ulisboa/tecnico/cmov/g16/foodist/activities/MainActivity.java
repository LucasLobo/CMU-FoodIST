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
import pt.ulisboa.tecnico.cmov.g16.foodist.model.FoodCourse;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.MenuItemAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.TypeOfFood;

public class MainActivity extends AppCompatActivity {

    private Menu menu;
    ListView listView;
    MenuItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        menu = new Menu();
        adapter = new MenuItemAdapter(this, menu.getMenuList());
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);
        menu.addMenuItem("Menu A", 2, TypeOfFood.MEAT,true, "dkajsdkajdaksdjladkas", FoodCourse.MAIN);
        menu.addMenuItem("Menu B", 2, TypeOfFood.FISH,true, "knmsokmdaosdjasdia", FoodCourse.MAIN);
        menu.addMenuItem("Menu C", 2, TypeOfFood.VEGETARIAN,true, "abcd", FoodCourse.MAIN);
        menu.addMenuItem("Menu D", 2, TypeOfFood.VEGAN,true, "qiwoehioqhe", FoodCourse.MAIN);


        Button buttonNewMenuItem = findViewById(R.id.buttonNewMenuItem);
        buttonNewMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.addMenuItem("abc", 2, TypeOfFood.MEAT,true, "abcd", FoodCourse.MAIN);
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
                Bundle extras = new Bundle();
                extras.putString("title", item.getName());
                extras.putInt("price", item.getPrice());
                extras.putInt("discount", item.getDiscount());
                extras.putString("foodType", item.getFoodType().toString());
                extras.putString("description", item.getDescription());
                //Image

                intent.putExtras(extras);
                startActivity(intent);

            }
        });

        adapter.getFilter().filter(" ");
    }
}
