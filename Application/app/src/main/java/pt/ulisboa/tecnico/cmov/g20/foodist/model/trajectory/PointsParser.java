package pt.ulisboa.tecnico.cmov.g20.foodist.model.trajectory;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PointsParser extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    TaskLoadedCallback taskCallback;
    String directionMode;

    DataParser parser;
    JSONObject jObject;

    public PointsParser(Context mContext, String directionMode) {
        this.taskCallback = (TaskLoadedCallback) mContext;
        this.directionMode = directionMode;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... strings) {

        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(strings[0]);
            parser = new DataParser();

            // Starts parsing data
            routes = parser.parse(jObject);

        } catch (Exception e) {
            Log.d("mylog", e.toString());
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        PolylineOptions lineOptions = null;
        lineOptions = getPath(result);
        // Drawing polyline in the Google Map for the i-th route
        HashMap<String, String> durationAndDistance = parser.getDuration(jObject);
        taskCallback.onTaskDone(lineOptions, durationAndDistance.get("duration"), durationAndDistance.get("distance"));

    }

    private PolylineOptions getPath(List<List<HashMap<String, String>>> result){
        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);
            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));

                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);
            Log.d("mylog", "onPostExecute lineoptions decoded");
        }
        return lineOptions;
    }

}
