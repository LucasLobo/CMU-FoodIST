package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.protobuf.ByteString;
import com.grpc.Contract;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.Runnable.SaveImageRunnable;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.grpc.util.Chunks;

public class MenuItemActivity extends AppCompatActivity {

    private static final String TAG = "MenuItemActivity";

    private static final int PICK_IMAGE = 100;
    private Data data;
    private MenuItem item;
    private LinearLayout imageLayout;

    private int foodServiceId;
    private int menuItemId;

    ImageView testView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();
        imageLayout = findViewById(R.id.imageSlots);
        Intent intent = getIntent();

        menuItemId = intent.getIntExtra("menuItemId", -1);
        foodServiceId = intent.getIntExtra("foodServiceId", -1);

        if (foodServiceId == -1 || menuItemId == -1) {
            finish();
        }

        Menu menu = data.getFoodService(foodServiceId).getMenu();
        item = menu.getMenuItem(menuItemId);

        setupView();
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
        ((TextView) findViewById(R.id.title)).setText(item.getName());
        ((TextView) findViewById(R.id.price)).setText(String.valueOf(item.getPrice()));
        ((TextView) findViewById(R.id.foodType)).setText(item.getFoodType().resourceId);
        ((TextView) findViewById(R.id.description)).setText(item.getDescription());

        Button importImageButton = findViewById(R.id.importImageButton);
        importImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });

        ArrayList<Bitmap> list = item.getImages();
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = list.get(i);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            setUpImageView(imageView, i);
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
                item.addImage(bitmap);
                setUpImageView(imageView, item.getImages().size() - 1);
                saveImage(foodServiceId, menuItemId, bitmap);
            }
        }
    }

    private void setUpImageView(ImageView imageView, final int index) {
        imageView.setVisibility(View.VISIBLE);
        imageLayout.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuItemActivity.this, FullscreenImageActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("foodServiceId", foodServiceId);
                extras.putInt("menuItemId", menuItemId);
                extras.putInt("imageIndex", index);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    public void saveImage(Integer foodServiceId, Integer menuItemId, Bitmap bitmap){
        new GrpcTask(new SaveImageRunnable(foodServiceId, menuItemId, bitmap) {
            @Override
            protected void callback(Bitmap bitmapResult) {}
        }).execute();
    }

}
