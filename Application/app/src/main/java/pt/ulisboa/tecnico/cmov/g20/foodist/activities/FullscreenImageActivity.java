package pt.ulisboa.tecnico.cmov.g20.foodist.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g20.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g20.foodist.R;

public class FullscreenImageActivity extends AppCompatActivity {

    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = (Data) getApplicationContext();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String menuName = extras.getString("menuName");
        Integer imageId = extras.getInt("imageId");

        Bitmap bitmap = data.getImage(imageId);
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        setTitle(menuName);
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
