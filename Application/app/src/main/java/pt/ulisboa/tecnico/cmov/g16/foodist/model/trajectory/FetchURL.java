package pt.ulisboa.tecnico.cmov.g16.foodist.model.trajectory;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.g16.foodist.R;

public class FetchURL extends AsyncTask<String, Void, String> {
    TaskLoadedCallback taskCallback;
    Context mContext;
    String directionMode = "walking";
    String includePath = "true";
    String foodServiceId;

    public FetchURL(Context mContext) {
        this.mContext = mContext;
        this.taskCallback = (TaskLoadedCallback) mContext;
    }

    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service
        String data = "";
        includePath = strings[1];
        if(strings.length > 2)
            foodServiceId = strings[2];
        try {
            // Fetching the data from web service
            data = downloadUrl(strings[0]);

        } catch (Exception e) {
        }
        return data;
    }

    @Override
    protected void onPostExecute(String jSonString) {
        super.onPostExecute(jSonString);
        if(Boolean.valueOf(includePath)) {
            PointsParser parserTask = new PointsParser(mContext, directionMode);
            // Invokes the thread for parsing the JSON data
            parserTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jSonString, includePath);
        }else{
            try {
                DataParser parser = new DataParser();
                HashMap<String, String> durationAndDistance = parser.getDuration(new JSONObject(jSonString));
                taskCallback.onTaskDone(null, durationAndDistance.get("duration"), durationAndDistance.get("distance"), foodServiceId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("mylog", "Downloaded URL: " + data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public String getDirectionsUrl(LatLng origin, LatLng destination) {
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+destination.latitude+","+destination.longitude;

        // Mode
        String mode = "mode=" + directionMode;

        // Key
        String key = "key=" + mContext.getString(R.string.google_directions_key);

        String departureTime = "departure_time=now";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + departureTime + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        Log.d("abcd", url);
        return url;
    }
}
