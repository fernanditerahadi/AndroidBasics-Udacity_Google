package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferencesFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener{

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummary(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummary(orderBy);

            Preference limit = findPreference(getString(R.string.settings_limit_key));
            bindPreferenceSummary(limit);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String preferenceString = (String) o;
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int preferenceIndex = listPreference.findIndexOfValue(preferenceString);
                if (preferenceIndex >= 0) {
                    preference.setSummary(listPreference.getEntries()[preferenceIndex]);
                }
            }
            else {
                preference.setSummary(preferenceString + " Richter Scale");
            }
            return true;
        }

        private void bindPreferenceSummary(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = sp.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);

        }
    }
}
