package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;

import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;

public class MenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private Data data;
    private MenuItem item;
    private LinearLayout imageLayout;

    private int foodServiceIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        data = (Data) getApplicationContext();
        imageLayout = findViewById(R.id.imageSlots);
        Intent intent = getIntent();
        intent.getIntExtra("itemIndex", -1);
        foodServiceIndex = intent.getIntExtra("foodServiceIndex", -1);


        Menu menu = data.getFoodService(foodServiceIndex).getMenu();
        item = menu.getMenuList().get(intent.getIntExtra("itemIndex", -1));
        setupView(item);

    }

    private void setupView(MenuItem item){
        ((TextView) findViewById(R.id.title)).setText(item.getName());
        ((TextView) findViewById(R.id.price)).setText(item.getPrice()+ "");
        ((TextView) findViewById(R.id.discount)).setText(item.getDiscount() + "");
        ((TextView) findViewById(R.id.foodType)).setText(item.getFoodType().toString());
        ((TextView) findViewById(R.id.description)).setText(item.getDescription());

        Button importImageButton = findViewById(R.id.importImageButton);
        importImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        LinkedList<ImageView> list = item.getImages();
        for(int i = 0; i!= list.size(); i++) {
            ImageView imageView = list.get(i);
            ((ViewGroup)imageView.getParent()).removeView(imageView);
            showImage(imageView);
        }
    }

    private void setupImage(final ImageView imageView){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuItemActivity.this, FullscreenImageActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("foodServiceIndex", foodServiceIndex);
                extras.putInt("imageIndex", item.getImages().indexOf(imageView));
                extras.putInt("itemIndex", data.getFoodService(foodServiceIndex).getMenu().getMenuList().indexOf(item));
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    private void chooseImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK  && requestCode == PICK_IMAGE){
            if(data != null){
                ImageView imageView = addImage(data.getData());
                showImage(imageView);
            }
        }
    }

    private ImageView addImage(Uri contentURI){
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(contentURI);
        item.getImages().add(imageView);
        return imageView;

    }

    private void showImage(ImageView imageView){
        imageView.setVisibility(View.VISIBLE);
        imageLayout.addView(imageView);
        setupImage(imageView);
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
