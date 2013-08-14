package com.swiggins.rssfordashclock;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import org.developerworks.android.Message;
import org.developerworks.android.AndroidSaxFeedParser;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;
import android.net.Uri;

import android.util.Log;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Collections;
import java.net.URL;

public class RssDashClockService extends DashClockExtension {

    private static final String TAG = "RssDashClockExtension";
    private static final String PREF_FEED = "pref_feed";
    private static final String PREF_UPDATE_SCREEN = "pref_update_screen";
    private static final String PREF_SYNC_FREQUENCY = "pref_sync_frequency";

    protected static List<String> links = new ArrayList<String>();
    private static Queue<Message> feedqueue = new LinkedList<Message>();
    private long lastUpdate = 0;

    protected void onInitialize(boolean isReconnect) {

		Log.d("swiggins", "In onInitialize(true)");

		SharedPreferences prefs = this.getSharedPreferences("com.swiggins.rssfordashclock", Context.MODE_PRIVATE);
        boolean updateOnAppearance = prefs.getBoolean(PREF_UPDATE_SCREEN, true);

		updateFeeds();
        
		this.setUpdateWhenScreenOn(updateOnAppearance);

    }

    protected void updateFeeds() {

		Log.d("swiggins", "In updateFeeds()");

		lastUpdate = new java.util.Date().getTime();
		AndroidSaxFeedParser feed = null;

		try {
			while (!feedqueue.isEmpty()) {
				Log.d("swiggins", "Clearing queue");
				feedqueue.remove();
			}

			for (String link : links) {
				feed = new AndroidSaxFeedParser(link);
				if (feed != null)
					for (Message m : feed.parse()) {
						Log.d("swiggins", "Adding message " + m.getTitle());
						feedqueue.add(m);
					}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		//Collections.sort(feedqueue);

    }
	
    protected void onUpdateData(int reason) {

        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_extension_example)
            .status("Rss")
            .expandedTitle((feedqueue.isEmpty()) ? "Error grabbing feeds" : feedqueue.peek().getTitle())
            .expandedBody((feedqueue.isEmpty()) ? "Error grabbing feeds" : feedqueue.peek().getDescription())
            .clickIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse((feedqueue.isEmpty()) ? "Error grabbing feeds" : feedqueue.peek().getLink().toString()))));

		if ((new java.util.Date()).getTime() - lastUpdate > 3600000)
            updateFeeds();
        else
			if (!feedqueue.isEmpty()) {
				Log.d("swiggins", "Displaying: " + feedqueue.peek().getTitle());
				feedqueue.poll();
				Log.d("swiggins", "Up next: " + feedqueue.peek().getTitle());
			}
			else 
				onInitialize(true);

    }
}
