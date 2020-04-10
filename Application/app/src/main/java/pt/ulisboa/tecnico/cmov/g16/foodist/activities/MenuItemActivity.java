package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class MenuItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        ((TextView) findViewById(R.id.title)).setText(extras.getString("title"));
        ((TextView) findViewById(R.id.price)).setText(extras.getInt("price") + "");
        ((TextView) findViewById(R.id.discount)).setText(extras.getInt("discount")+ "");
        ((TextView) findViewById(R.id.foodType)).setText(extras.getString("foodType"));
        ((TextView) findViewById(R.id.description)).setText(extras.getString("description"));
    }


}
