package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.trajectory.FetchURL;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.trajectory.TaskLoadedCallback;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private LatLng destination;
    private Polyline mPolyline;

    private String distance = "UNKNOWN";
    private String arrivalTime = "UNKNOWN";

    private int LOCATION_PERMISSION_ID = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        String name = intent.getStringExtra("foodServiceName");
        setTitle(name);
        double destLat = intent.getDoubleExtra("Latitude", CampusLocation.Campus.ALAMEDA.latitude); // Change to UNKNOWN - after changing null UNKNOWN's value
        double destLog = intent.getDoubleExtra("Longitude", CampusLocation.Campus.ALAMEDA.longitude); // Change to UNKNOWN - after changing null UNKNOWN's value
        destination = new LatLng(destLat, destLog);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (!checkPermissions()){
            requestPermissions();
        }
        @SuppressLint("MissingPermission") Location myLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        mMap.setMinZoomPreference(15);
        mMap.setMaxZoomPreference(25);
        drawRoute(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), destination);
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        FetchURL fetchURL = new FetchURL(MapActivity.this);
        String url = fetchURL.getDirectionsUrl(origin, destination);
        fetchURL.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR ,url, "true");
    }


    @Override
    public void onTaskDone(Object... values) { // Está kinda sempre a calcular.
        if(values[0] != null){
            if(mPolyline != null)
                mPolyline.remove();
            mPolyline = mMap.addPolyline((PolylineOptions) values[0]);
            arrivalTime = (String) values[1];
            distance = (String) values[2];
        }
        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        options.title("Duration: " + arrivalTime);
        options.snippet("Distance: " + distance);
        mMap.addMarker(options);
    }
    private boolean checkPermissions() { //did the user grant permission
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() { //request permission if not already granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
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
