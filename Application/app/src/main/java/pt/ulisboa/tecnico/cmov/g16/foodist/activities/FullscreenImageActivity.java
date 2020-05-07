package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class FullscreenImageActivity extends AppCompatActivity {

    Data data;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = (Data) getApplicationContext();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        image= data.getFoodService(extras.getInt("foodServiceIndex")).getMenu().getMenuList().get(extras.getInt("itemIndex")).getImages().get(extras.getInt("imageIndex"));
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(image.getDrawable());
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
}
