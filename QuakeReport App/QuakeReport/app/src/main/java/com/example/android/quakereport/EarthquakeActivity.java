/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.quakereport.Adapters.EarthquakeAdapter;
import com.example.android.quakereport.Loaders.EarthquakeLoader;
import com.example.android.quakereport.Models.EarthquakeData;
import com.example.android.quakereport.Utilities.QueryUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class EarthquakeActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<EarthquakeData>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter mEarthquakeAdapter;
    private ListView mEarthquakeView;
    private TextView mEmptyView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        mEarthquakeView = (ListView) findViewById(R.id.list);

        mEarthquakeAdapter = new EarthquakeAdapter(this, new ArrayList<EarthquakeData>());

        mEarthquakeView.setAdapter(mEarthquakeAdapter);
        mEarthquakeView.setOnItemClickListener(this);

        if (getConectivityStatus()) {
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            mEmptyView.setText(R.string.no_internet_connection);
            showEmptyView();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        EarthquakeData currEarthquakeData = (EarthquakeData) adapterView.getItemAtPosition(i);
        Uri currEarthquakeUri = currEarthquakeData.getUri();
        Intent startWebIntent = new Intent(Intent.ACTION_VIEW, currEarthquakeUri);
        startActivity(startWebIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            Intent settingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<EarthquakeData>> onCreateLoader(int i, Bundle bundle) {
        return new EarthquakeLoader(EarthquakeActivity.this,
                getUriFromUsersPreferences().toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<EarthquakeData>> loader,
                               ArrayList<EarthquakeData> earthquakeData) {
        mEarthquakeAdapter.clear();
        if (earthquakeData != null) {
            if (!earthquakeData.isEmpty()) {
                mEarthquakeAdapter.addAll(earthquakeData);
                showEarthquakeView();
                Toast.makeText(this, "Found " + earthquakeData.size() + " number of records",
                        Toast.LENGTH_LONG).show();
            }
            else {
                mEmptyView.setText(R.string.no_earthquake_found);
                showEmptyView();
            }
        }
        else {
            mEmptyView.setText(R.string.no_earthquake_found);
            showEmptyView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<EarthquakeData>> loader) {
        mEarthquakeAdapter.clear();
    }

    private boolean getConectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private Uri.Builder getUriFromUsersPreferences() {
        HashMap<String, String> usersPreferences = new HashMap<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sp.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sp.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String limit = sp.getString(
                getString(R.string.settings_limit_key),
                getString(R.string.settings_limit_default));
        usersPreferences.put(getString(R.string.query_min_magnitude), minMagnitude);
        usersPreferences.put(getString(R.string.query_order_by), orderBy);
        usersPreferences.put(getString(R.string.query_limit), limit);
        return QueryUtils.createUriFromUsersPreferences(usersPreferences);
    }

    private void showEmptyView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
        mEarthquakeView.setVisibility(View.INVISIBLE);
    }

    private void showEarthquakeView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mEarthquakeView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }
}
