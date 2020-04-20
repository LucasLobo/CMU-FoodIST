package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.ulisboa.tecnico.cmov.g16.foodist.Data;
import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.adapters.FoodServiceListRecyclerAdapter;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.User;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private int LOCATION_PERMISSION_ID = 44;

    Data data;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = (Data) getApplicationContext();
        user = data.getUser();

        Button menuButton = findViewById(R.id.menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            }
        });

        initToolbar();
        initFoodServiceRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        initLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission accepted", Toast.LENGTH_SHORT).show();
                proceedLocationPermissionGranted();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton userProfile = findViewById(R.id.userProfile);
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initFoodServiceRecyclerView() {
        RecyclerView rv = findViewById(R.id.food_service_list);
        FoodServiceListRecyclerAdapter adapter = new FoodServiceListRecyclerAdapter(this, data.getFoodServiceList());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }


    private void initLocation() {
        if (checkPermissions()) {
            proceedLocationPermissionGranted();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() { //did the user grant permission
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() { //request permission if not already granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
    }

    public void proceedLocationPermissionGranted() {
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Location is not turned on.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
        else {
            proceedLocationLocationEnabled();
        }
    }

    private boolean isLocationEnabled() { //checks if user has location  turned on
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void proceedLocationLocationEnabled() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(0);
        locationRequest.setFastestInterval(0);
        locationRequest.setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                user.setLocation(locationResult.getLastLocation());

            }
        }, Looper.myLooper());
    }

    public void openTermiteActivity(View view) {
        Log.i(TAG, "hello");
        Intent intent = new Intent(this, PeerScannerActivity.class);
        startActivity(intent);
    }
}
