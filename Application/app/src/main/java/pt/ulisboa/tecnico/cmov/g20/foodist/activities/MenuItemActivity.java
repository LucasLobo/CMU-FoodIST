package pt.ulisboa.tecnico.cmov.g20.foodist.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuInflater;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Set;

import pt.ulisboa.tecnico.cmov.g20.foodist.adapters.MenuItemImagesAdapter;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g20.foodist.R;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.FoodService;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.Menu;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.MenuItem;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.GrpcTask;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.grpc.Runnable.SaveImageRunnable;

public class MenuItemActivity extends AppCompatActivity {

    private static final String TAG = "MenuItemActivity";

    private static final int PICK_IMAGE = 100;

    private CardView cardView;
    private RecyclerView recyclerView;
    private Data data;
    private FoodService foodService;
    private MenuItem menuItem;

    private MenuItemImagesAdapter adapter;

    private int foodServiceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = (Data) getApplicationContext();

        Intent intent = getIntent();

        int menuItemId = intent.getIntExtra("menuItemId", -1);
        foodServiceId = intent.getIntExtra("foodServiceId", -1);

        if (foodServiceId == -1 || menuItemId == -1) {
            finish();
        }

        foodService = data.getFoodService(foodServiceId);
        Menu menu = foodService.getMenu();
        menuItem = menu.getMenuItem(menuItemId);
        setTitle(menuItem.getName());

        setupRecycler();
        setupView();
        adapter.notifyDataSetChanged();

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

            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if(menuItem.getImageIds().size() != 0) {
                    Integer randomImageId = menuItem.getRandomImageId();
                    Bitmap image = data.getImage(randomImageId);
                    Uri imgUri = getImageUri(image);

                    sendIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                }

                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Let's try the " + menuItem.getName() + " at " + foodService.getName() +
                        "\nFood Type: " + getString(menuItem.getFoodType().resourceId) +
                        "\nPrice: " + getString(R.string.price_value,menuItem.getPrice()) +
                        "\nhttp://foodist.pt/menuitem/" + foodService.getId() + "/" + menuItem.getId());
                sendIntent.setType("*/*");

                Intent shareIntent = Intent.createChooser(sendIntent, "Share:");
                startActivity(shareIntent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_food_service_share, menu);
        return true;
    }

    private void setupView(){
        ((TextView) findViewById(R.id.title)).setText(menuItem.getName());
        ((TextView) findViewById(R.id.menu_item_price_value)).setText(getString(R.string.price_value, menuItem.getPrice()));
        ((TextView) findViewById(R.id.menu_item_food_type_value)).setText(menuItem.getFoodType().resourceId);

        String description = menuItem.getDescription();
        if (description.isEmpty()) {
            (findViewById(R.id.description)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.description)).setText(menuItem.getDescription());
        }

        Button importImageButton = findViewById(R.id.importImageButton);
        importImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromGallery();
            }
        });
    }

    private void setupRecycler() {
        recyclerView = findViewById(R.id.imageListView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        Set<Integer> imageIds = menuItem.getImageIds();
        adapter = new MenuItemImagesAdapter(this, new ArrayList<>(imageIds), menuItem.getName());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        cardView = findViewById(R.id.menu_item_recyclerview_holder);
        if (imageIds.size() == 0) cardView.setVisibility(View.GONE);
        else cardView.setVisibility(View.VISIBLE);
    }


    private void chooseImageFromGallery() {
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
                saveImage(foodServiceId, menuItem, bitmap);
            }
        }
    }


    public void saveImage(Integer foodServiceId, final MenuItem menuItem, final Bitmap bitmap) {
        new GrpcTask(new SaveImageRunnable(foodServiceId, menuItem.getId(), bitmap) {
            @Override
            protected void callback(Integer imageId) {
                menuItem.addImageId(imageId);
                data.addImage(imageId, bitmap);
                adapter.addImage(imageId);
                cardView.setVisibility(View.VISIBLE);
            }
        }).execute();
    }


    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
