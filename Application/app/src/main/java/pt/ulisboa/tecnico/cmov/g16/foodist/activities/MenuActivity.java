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

import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.MenuItemAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class MenuActivity extends AppCompatActivity {

    private Menu menu;
    Data data;
    User user;
    ListView listView;
    MenuItemAdapter adapter;
    private int foodServiceIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        foodServiceIndex = intent.getIntExtra("index", -1);
        if(foodServiceIndex == -1)
            finish();
        data = (Data) getApplicationContext();
        menu = data.getFoodService(foodServiceIndex).getMenu();
        user = data.getUser();

        adapter = new MenuItemAdapter(this, menu.getMenuList());
        listView = findViewById(R.id.list);
        listView.setAdapter(adapter);


        Button buttonNewMenuItem = findViewById(R.id.buttonNewMenuItem);
        buttonNewMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, NewMenuItemActivity.class);
                intent.putExtra("foodServiceIndex", foodServiceIndex);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItem item = adapter.getItem(i);
                Intent intent = new Intent(MenuActivity.this, MenuItemActivity.class);
                intent.putExtra("foodServiceIndex", foodServiceIndex);
                intent.putExtra("itemIndex", menu.getMenuList().indexOf(item));
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setDietaryConstraints(user.getDietaryConstraints());
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
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return true;
    }
}
