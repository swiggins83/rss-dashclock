package com.swiggins.rssfordashclock;

import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;

import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

public class RssDashClockSettingsActivity extends PreferenceActivity {

    String pref_sync_frequency;
    boolean pref_update_screen;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setIcon(R.drawable.ic_extension_example);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.pref_example);

		final SharedPreferences prefs = this.getSharedPreferences("com.swiggins.rssfordashclock", Context.MODE_PRIVATE);
        final CheckBoxPreference checkbox = (CheckBoxPreference) getPreferenceManager().findPreference("pref_update_screen");
        checkbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference pref, Object newValue) {
                Log.d("swiggins", "Pref " + pref.getKey() + " changed to " + newValue.toString());
                return true;
            }
        });

		final EditTextPreference editText = (EditTextPreference) getPreferenceManager().findPreference("pref_feed");
		editText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pref, Object newValue) {
				Log.d("swiggins", "Pref " + pref.getKey() + " changed to " + newValue.toString());
				prefs.edit().putString(pref.getKey(), newValue.toString()).commit();
				return true;
			}
		});
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //private void getPrefs() {
    //    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    //    pref_sync_frequency = prefs.getString("pref_sync_frequency", "1");
    //    pref_update_screen = prefs.getBoolean("pref_update_screem", true);
    //}
}
