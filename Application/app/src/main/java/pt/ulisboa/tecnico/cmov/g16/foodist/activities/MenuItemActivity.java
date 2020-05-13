package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.SaveImageRunnable;

public class MenuItemActivity extends AppCompatActivity {

    private static final String TAG = "MenuItemActivity";

    private static final int PICK_IMAGE = 100;
    private MenuItem menuItem;
    private LinearLayout imageLayout;

    Data data;
    private int foodServiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();
        imageLayout = findViewById(R.id.imageSlots);
        Intent intent = getIntent();

        int menuItemId = intent.getIntExtra("menuItemId", -1);
        foodServiceId = intent.getIntExtra("foodServiceId", -1);

        if (foodServiceId == -1 || menuItemId == -1) {
            finish();
        }

        Menu menu = data.getFoodService(foodServiceId).getMenu();
        menuItem = menu.getMenuItem(menuItemId);
        setTitle(menuItem.getName());
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        data.fetchMenuImages(menuItem);
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

    private void setupView(){
        ((TextView) findViewById(R.id.title)).setText(menuItem.getName());
        ((TextView) findViewById(R.id.menu_item_price_value)).setText(getString(R.string.price_value, menuItem.getPrice()));
        ((TextView) findViewById(R.id.menu_item_food_type_value)).setText(menuItem.getFoodType().resourceId);
        ((TextView) findViewById(R.id.description)).setText(menuItem.getDescription());

        Button importImageButton = findViewById(R.id.importImageButton);
        importImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        for (Integer imageId : menuItem.getImageIds()) {
            Bitmap bitmap = data.getImage(imageId);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            setUpImageView(imageView, imageId);
        }
    }


    private void chooseImageFromGallery(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK  && requestCode == PICK_IMAGE){
            if (data != null) {
                Uri uri = data.getData();
                ImageView imageView = new ImageView(this);
                imageView.setImageURI(uri);

                final Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                saveImage(foodServiceId, menuItem, bitmap, imageView);
            }
        }
    }

    private void setUpImageView(ImageView imageView, final int imageId) {
        imageView.setVisibility(View.VISIBLE);
        imageLayout.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuItemActivity.this, FullscreenImageActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("imageId", imageId);
                extras.putString("menuName", menuItem.getName());
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    public void saveImage(Integer foodServiceId, final MenuItem menuItem, final Bitmap bitmap, final ImageView imageView){
        new GrpcTask(new SaveImageRunnable(foodServiceId, menuItem.getId(), bitmap) {
            @Override
            protected void callback(Integer imageId) {
                menuItem.addImageId(imageId);
                data.addImage(imageId, bitmap);
                setUpImageView(imageView, imageId);
            }
        }).execute();
    }

}
