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
import android.preference.PreferenceManager;

import android.util.Log;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Collections;
import java.net.URL;

public class RssDashClockService extends DashClockExtension
{
    private static final String TAG = "RssDashClockExtension";
    public static final String PREF_FEED = "pref_feed";
    public static final String PREF_UPDATE_SCREEN = "pref_update_screen";
    public static final String PREF_SYNC_FREQUENCY = "pref_sync_frequency";

    private List<String> links = new ArrayList<String>();
    private Stack<Message> feedstack = new Stack<Message>();
    private long lastUpdate = 0;

    protected void onInitialize(boolean isReconnect)
    {
        boolean newFeed = true;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean updateOnAppearance = sp.getBoolean(PREF_UPDATE_SCREEN, true);
        String addFeed = sp.getString(PREF_FEED, getString(R.string.pref_feed));

        if (Arrays.asList(links).contains(addFeed) && !addFeed.equals("Enter a URL")) {
            newFeed = false;
            Log.d("swiggins",addFeed + " is not being added.");
        }

        links.add("https://news.ycombinator.com/rss");
        links.add("http://cube-drone.com/rss.xml");

        if (newFeed == true) {
            links.add(addFeed);
            Log.d("swiggins",addFeed + " is being added.");
        }

        updateFeeds();
        
        Log.d("swiggins","setUpdateWhenScreenOn("+updateOnAppearance+")");
        this.setUpdateWhenScreenOn(updateOnAppearance);
    }

    protected void updateFeeds()
    {
        lastUpdate = new java.util.Date().getTime();
        AndroidSaxFeedParser feed = null;

        try
        {
            while (!feedstack.isEmpty())
            {
                feedstack.pop();
            }

            for (String link : links)
            {
                feed = new AndroidSaxFeedParser(link);
                for (Message m : feed.parse())
                    feedstack.push(m);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Collections.sort(feedstack);
    }

    protected void onUpdateData(int reason)
    {
        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_extension_example)
            .status("Rss")
            .expandedTitle(feedstack.peek().getTitle())
            .expandedBody(feedstack.peek().getDescription())
            .clickIntent(new Intent(Intent.ACTION_VIEW,
                Uri.parse((feedstack.isEmpty()) ? "Error grabbing feeds" : feedstack.peek().getLink().toString()))));

        if (feedstack.isEmpty() || (new java.util.Date()).getTime() - lastUpdate > 3600000)
            updateFeeds();
        else
            feedstack.pop();
    }
}
