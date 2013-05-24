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

import java.util.Stack;
import java.util.Collections;
import java.net.URL;

public class RssDashClockService extends DashClockExtension
{
    private static final String TAG = "RssDashClockExtension";
    public static final String PREF_NAME = "pref_name";

    private String[] links = { "http://forum.xda-developers.com/external.php?type=RSS2", "http://www.reddit.com/.rss", "https://news.ycombinator.com/rss" };
    private Stack<Message> feedstack = new Stack<Message>();
    private long lastUpdate = 0;

    protected void onInitialize(boolean isReconnect)
    {
        //SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        //String feeds = sp.getString(PREF_NAME, getString(R.string.feedurls));
        updateFeeds();
        this.setUpdateWhenScreenOn(true);
    }

    protected void updateFeeds()
    {
        AndroidSaxFeedParser feed = null;

        for (String link : links)
        {
            try
            {
                feed = new AndroidSaxFeedParser(link);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            for (Message m : feed.parse())
                feedstack.push(m);
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
                Uri.parse(feedstack.peek().getLink().toString()))));

        if (feedstack.isEmpty())
            updateFeeds();
        else
            feedstack.pop();
    }
}
