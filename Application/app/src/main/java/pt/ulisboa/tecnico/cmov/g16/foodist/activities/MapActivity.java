package pt.ulisboa.tecnico.cmov.g16.foodist.activities;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.CampusLocation;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.tracjectory.DirectionsJSONParser;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.tracjectory.FetchURL;
import pt.ulisboa.tecnico.cmov.g16.foodist.model.tracjectory.TaskLoadedCallback;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private LatLng destination;
    private Polyline mPolyline;

    private int LOCATION_PERMISSION_ID = 45;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        double destLat = intent.getDoubleExtra("Latitude", CampusLocation.Campus.TAGUS.latitude); // Change to UNKNOWN - after changing null UNKNOWN's value
        double destLog = intent.getDoubleExtra("Longitude", CampusLocation.Campus.TAGUS.longitude); // Change to UNKNOWN - after changing null UNKNOWN's value
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
        mMap.setMinZoomPreference(17);
        mMap.setMaxZoomPreference(25);
        drawRoute(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), destination);
    }

    private void drawRoute(LatLng origin, LatLng destination) {
        String url = getDirectionsUrl(origin, destination);
        Log.d("abcd", url);
        new FetchURL(MapActivity.this).execute(url, "walking");
    }

    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+destination.latitude+","+destination.longitude;

        // Mode
        String mode = "mode=walking";

        // Key
        String key = "key=" + getString(R.string.google_directions_key);

        String departureTime = "departure_time=now";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + departureTime + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (mPolyline != null)
            mPolyline.remove();
        mPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        options.title("Duration: " + values[1]);
        options.snippet("Distance: " + values[2]);
        mMap.addMarker(options);
    }
    private boolean checkPermissions() { //did the user grant permission
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() { //request permission if not already granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
    }


}
