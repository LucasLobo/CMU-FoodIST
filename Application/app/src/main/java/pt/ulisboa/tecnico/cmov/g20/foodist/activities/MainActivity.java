package pt.ulisboa.tecnico.cmov.g20.foodist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import pt.ulisboa.tecnico.cmov.g20.foodist.model.Data;
import pt.ulisboa.tecnico.cmov.g20.foodist.R;
import pt.ulisboa.tecnico.cmov.g20.foodist.adapters.FoodServiceListRecyclerAdapter;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.User;
import pt.ulisboa.tecnico.cmov.g20.foodist.model.trajectory.TaskLoadedCallback;

public class MainActivity extends AppCompatActivity implements TaskLoadedCallback {
    private static final String TAG = "MainActivity";

    private int LOCATION_PERMISSION_ID = 44;

    private FoodServiceListRecyclerAdapter adapter;
    private Toolbar toolbar;
    private Data data;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        data = (Data) getApplicationContext();
        user = data.getUser();

        initToolbar();
        initFoodServiceRecyclerView();

        Intent receivedIntent = getIntent();
        if(Intent.ACTION_VIEW.equals(receivedIntent.getAction())){
            Uri uri = receivedIntent.getData();
            List<String> params = uri.getPathSegments();
            String isFoodService = params.get(0);
            String fsId = params.get(1);
            if(isFoodService.equals("foodservice")){
                Intent intent = new Intent(getApplicationContext(), FoodServiceActivity.class);
                intent.putExtra("id", Integer.valueOf(fsId));
                startActivity(intent);
            }
            else if(isFoodService.equals("menuitem")){
                String menuItemId = params.get(2);

                Intent intent = new Intent(getApplicationContext(), MenuItemActivity.class);
                intent.putExtra("menuItemId", Integer.valueOf(menuItemId));
                intent.putExtra("foodServiceId", Integer.valueOf(fsId));
                startActivity(intent);
            }


        }

        MapFragment mDummyMapInit = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mDummyMapInit.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //Do nothing;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        initLocation();
        adapter.setUserStatus(user.getStatus());
        adapter.setCampus(user.getCampus());
        adapter.setDietaryConstraints(user.getDietaryConstraints());
        adapter.updateList();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.userProfile:
                Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_toggle:
                adapter.toggleConstrainsFilter();
                user.toggleConstraintsFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initFoodServiceRecyclerView() {
        RecyclerView rv = findViewById(R.id.food_service_list);
        adapter = new FoodServiceListRecyclerAdapter(this, data.getFoodServiceList());
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
        if(user.isLocAuto()) {
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
                    adapter.setUserLocation(new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()));

                    if (user.getLocation().getCampus().equals(CampusLocation.Campus.UNKNOWN)) {
                        String[] options = {getString(CampusLocation.Campus.ALAMEDA.id), getString(CampusLocation.Campus.TAGUS.id), getString(CampusLocation.Campus.CTN.id)};

                        AlertDialog.Builder campusAlert = new AlertDialog.Builder(MainActivity.this);
                        campusAlert.setTitle("Could not find your Campus. Please select one:");
                        campusAlert.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    user.setCampus(CampusLocation.Campus.ALAMEDA);
                                } else if (which == 1) {
                                    user.setCampus(CampusLocation.Campus.TAGUS);
                                } else {
                                    user.setCampus(CampusLocation.Campus.CTN);
                                }

                                adapter.setCampus(user.getCampus());
                                adapter.updateList();
                                user.setLocAuto(false); //location finder set to manual
                                Toast.makeText(MainActivity.this, "Location Finder is in Manual mode", Toast.LENGTH_SHORT).show();
                            }
                        });
                        campusAlert.show();
                    } else {
                      adapter.setCampus(user.getCampus());
                      adapter.updateList();
                    }

                }
            }, Looper.myLooper());
        }
    }

    @Override
    public void onTaskDone(Object... values) {
        adapter.getFoodServicesDistanceTime().put(Integer.parseInt((String) values[3]), (String) values[1]);
        adapter.notifyDataSetChanged();
    }

}
