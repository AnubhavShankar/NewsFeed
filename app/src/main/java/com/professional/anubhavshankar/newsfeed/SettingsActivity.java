package com.professional.anubhavshankar.newsfeed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by Anubhav Shankar on 8/29/2016.
 */
public class SettingsActivity extends PreferenceActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content,new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            String category=getString(R.string.category);
            if(key.equals(category))
            {
                Preference pref=findPreference(category);
                pref.setSummary(sharedPreferences.getString(category,getString(R.string.default_category)));
            }
        }
    }
}
