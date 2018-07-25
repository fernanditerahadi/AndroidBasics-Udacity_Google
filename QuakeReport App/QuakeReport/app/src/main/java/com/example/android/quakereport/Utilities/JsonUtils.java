package com.example.android.quakereport.Utilities;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.quakereport.Models.EarthquakeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;


public final class JsonUtils {

    private JsonUtils() {
    }


    public static ArrayList<EarthquakeData> extractEarthquakes(String earthquakeJsonInput) {
        if (TextUtils.isEmpty(earthquakeJsonInput)) {
            return null;
        }

        ArrayList<EarthquakeData> earthquakes = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(earthquakeJsonInput);
            JSONArray features = root.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject object = features.getJSONObject(i);
                JSONObject properties = object.getJSONObject("properties");
                double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                String time = properties.getString("time");
                String url = properties.getString("url");
                earthquakes.add(new EarthquakeData(place, time, mag, url));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earth quake JSON input", e);
        }

        return earthquakes;
    }
}