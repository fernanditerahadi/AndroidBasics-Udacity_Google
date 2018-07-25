package com.example.android.quakereport.Utilities;

import android.net.Uri;

import java.util.HashMap;

public final class QueryUtils {

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private QueryUtils() {
    }

    public static Uri.Builder createUriFromUsersPreferences(HashMap<String, String> usersPreferences) {
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        for (String key : usersPreferences.keySet()) {
            uriBuilder.appendQueryParameter(key, usersPreferences.get(key));
        }
        return uriBuilder;
    }
}
