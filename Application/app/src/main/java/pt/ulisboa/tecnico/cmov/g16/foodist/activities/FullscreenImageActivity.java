package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class FullscreenImageActivity extends AppCompatActivity {

    Data data;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);
        data = (Data) getApplicationContext();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        image= data.getFoodService(extras.getInt("foodServiceIndex")).getMenu().getMenuList().get(extras.getInt("itemIndex")).getImages().get(extras.getInt("imageIndex"));
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageDrawable(image.getDrawable());
    }
}
