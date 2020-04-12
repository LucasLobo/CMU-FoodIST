package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.LinkedList;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.MenuItem;

public class MenuItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    Data data;
    MenuItem item;
    LinearLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        data = (Data) getApplicationContext();
        Menu menu = data.getMenu();
        imageLayout = findViewById(R.id.imageSlots);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        item = menu.getMenuList().get(extras.getInt("index"));
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

    }

}
