package com.swiggins.rssfordashclock;

import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceCategory;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;


public class RssDashClockSettingsActivity extends PreferenceActivity {

    String pref_sync_frequency;
    boolean pref_update_screen;
	ArrayAdapter<String> adapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setIcon(R.drawable.ic_extension_example);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        addPreferencesFromResource(R.xml.pref);

		final SharedPreferences prefs = this.getSharedPreferences("com.swiggins.rssfordashclock", Context.MODE_PRIVATE);
        final CheckBoxPreference checkbox = (CheckBoxPreference) getPreferenceManager().findPreference("pref_update_screen");
        checkbox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference pref, Object newValue) {
				Log.d("swiggins", pref.getKey()+"="+newValue.toString());
				prefs.edit().putBoolean(pref.getKey(), ((Boolean) newValue).booleanValue()).commit();
                return true;
            }
        });

		final EditTextPreference editText = (EditTextPreference) getPreferenceManager().findPreference("pref_feed");
		editText.setOnPreferenceChangeListener(
			new Preference.OnPreferenceChangeListener() {
			public boolean onPreferenceChange(Preference pref, Object newValue) {
				prefs.edit().putString(pref.getKey(), newValue.toString()).commit();
				if (!RssDashClockService.links.contains(newValue.toString())) {
					Log.d("swiggins", "links.add("+newValue.toString()+")");
					RssDashClockService.links.add(newValue.toString());

					for (String link : RssDashClockService.links)
						Toast.makeText(RssDashClockSettingsActivity.this, link, Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_menu, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.menu.add_menu:
				addToList();
				return true;
			default: 
				Log.d("swiggins", "Default case?");
				return super.onOptionsItemSelected(item);
		}
    }

	public void addToList() {
		
	}

}
