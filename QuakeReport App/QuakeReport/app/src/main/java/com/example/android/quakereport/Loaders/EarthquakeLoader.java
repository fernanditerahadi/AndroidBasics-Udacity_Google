package com.example.android.quakereport.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.quakereport.Models.EarthquakeData;
import com.example.android.quakereport.Utilities.NetworkUtils;

import java.util.ArrayList;

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<EarthquakeData>>{

    private String mRequestURL;

    public EarthquakeLoader(Context context, String requestURL) {
        super(context);
        mRequestURL = requestURL;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<EarthquakeData> loadInBackground() {
        return NetworkUtils.fetchEarthquakeData(mRequestURL);
    }


}
