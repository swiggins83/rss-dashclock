package com.swiggins.rssfordashclock;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import org.developerworks.android.Message;
import org.developerworks.android.AndroidSaxFeedParser;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.net.Uri;
import android.content.SharedPreferences;
import android.content.Context;
import android.preference.PreferenceManager;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.net.URL;

public class RssDashClockService extends DashClockExtension
{
    private static final String TAG = "RssDashClockExtension";
    private static final String PREF_FEED = "pref_feed";
    private static final String PREF_UPDATE_SCREEN = "pref_update_screen";
    private static final String PREF_SYNC_FREQUENCY = "pref_sync_frequency";

    private static List<String> links = new ArrayList<String>();
    private static Stack<Message> feedstack = new Stack<Message>();
    private long lastUpdate = 0;

    protected void onInitialize(boolean isReconnect) {

		SharedPreferences prefs = this.getSharedPreferences("com.swiggins.rssfordashclock", Context.MODE_PRIVATE);
        boolean updateOnAppearance = prefs.getBoolean(PREF_UPDATE_SCREEN, true);
        String addFeed = prefs.getString(PREF_FEED, getString(R.string.pref_feed));

		if (addFeed.equals("Enter a URL"))
            Log.d("swiggins", addFeed + " is not being added.");
        else {
			if (links.contains(addFeed.toString()))
				Log.d("swiggins", addFeed + " is already in the list.");
			else {
				Log.d("swiggins", addFeed + " is being added.");
				links.add(addFeed);
				for (String link : links) {
					Log.d("swiggins", link);
				}

				updateFeeds();
			}
        }
        
		if (updateOnAppearance) {
			this.setUpdateWhenScreenOn(updateOnAppearance);
		} else {
			this.setUpdateWhenScreenOn(updateOnAppearance);
		}

    }

    protected void updateFeeds() {

		lastUpdate = new java.util.Date().getTime();
		AndroidSaxFeedParser feed = null;

		try {
			while (!feedstack.isEmpty())
				feedstack.pop();

			for (String link : links) {
				feed = new AndroidSaxFeedParser(link);
				for (Message m : feed.parse())
					feedstack.push(m);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		//Collections.sort(feedstack);

    }
	
    protected void onUpdateData(int reason) {

		onInitialize(true);

        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_extension_example)
            .status("Rss")
            .expandedTitle((feedstack.isEmpty()) ? "Error grabbing feeds" : feedstack.peek().getTitle())
            .expandedBody((feedstack.isEmpty()) ? "Error grabbing feeds" : feedstack.peek().getDescription())
            .clickIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse((feedstack.isEmpty()) ? "Error grabbing feeds" : feedstack.peek().getLink().toString()))));

		if ((new java.util.Date()).getTime() - lastUpdate > 3600000)
            updateFeeds();
        else
			if (!links.isEmpty()) {
				Log.d("swiggins", "pop!");
				feedstack.pop();
			}

    }
}
