package com.professional.anubhavshankar.newsfeed.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.professional.anubhavshankar.newsfeed.data.NewsFeed;
import com.professional.anubhavshankar.newsfeed.data.NewsFeedContract;
import com.professional.anubhavshankar.newsfeed.data.NewsFeedParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Created by Anubhav Shankar on 9/9/2016.
 */
public class ServiceIntent extends IntentService {
    private static final String LOG_TAG = ServiceIntent.class.getSimpleName();
    private static final String name="NewsFeedUpdater";
    public ServiceIntent() {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpURLConnection httpURLConnection=null;
        BufferedReader reader=null;
        List<NewsFeed> myNewsFeed=null;
        try{
            String url=intent.getStringExtra("url");
            Log.d(LOG_TAG, "Going to Parse...");
            Uri newsUri=Uri.parse(url);
            Log.d(LOG_TAG,"URI formed of "+ url);
            URL newsURL= new URL(newsUri.toString());
            httpURLConnection=(HttpURLConnection)newsURL.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            InputStream inputStream= httpURLConnection.getInputStream();
            reader= new BufferedReader(new InputStreamReader(inputStream));
            myNewsFeed= new NewsFeedParser().parse(reader);
            if (inputStream==null){
                Log.d(LOG_TAG,"INPUT STREAM NOT OPENTED");
                return;
            }

        } catch (IOException |XmlPullParserException e){
            Log.d(LOG_TAG,e.getMessage());
            e.printStackTrace();
            return ;
        }
        finally {
            if (httpURLConnection!=null)
                httpURLConnection.disconnect();
            cleanupDB();
        }
        ContentValues[] cvArray=createNewsForDB(myNewsFeed);
        int rows=getContentResolver().bulkInsert(NewsFeedContract.NewsEntry.CONTENT_URI,cvArray);
        Log.d(LOG_TAG,"Number of rows inserted into database"+rows);

        return;
    }
    public void cleanupDB(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-4);
        Date d=new Date(c.getTimeInMillis());
        Long dateVal=NewsFeed.getDBFormatDate(d);
        String selection= NewsFeedContract.NewsEntry.COLUMN_DATE+ " <= "+dateVal;
        int deletedRows=getContentResolver().delete(NewsFeedContract.NewsEntry.CONTENT_URI,selection,null);
        Log.d(LOG_TAG,"date chosen for cleanup is: " +d.toString());
        Log.d(LOG_TAG, "Number of rows Deleted: " + Integer.toString(deletedRows));
    }
    public ContentValues[] createNewsForDB(List<NewsFeed> newsFeeds){
        Vector<ContentValues> cv= new Vector<ContentValues>();
        //ContentValues temp= new ContentValues();
        for(NewsFeed news:newsFeeds){
            ContentValues temp= new ContentValues();
            temp.put(NewsFeedContract.NewsEntry.COLUMN_TITLE,news.getTitle());
            temp.put(NewsFeedContract.NewsEntry.COLUMN_STORY_IMAGE,news.getImageUrl());
            temp.put(NewsFeedContract.NewsEntry.COLUMN_Category,news.getCategory());
            temp.put(NewsFeedContract.NewsEntry.COLUMN_DETAIL, news.getDetailLink());
            temp.put(NewsFeedContract.NewsEntry.COLUMN_DATE, news.getDBDateString());
            cv.add(temp);
            Log.d(LOG_TAG,"TITLE ADDED TO CV VALUES"+temp.get(NewsFeedContract.NewsEntry.COLUMN_TITLE));
        }
        ContentValues[] cvArray= new ContentValues[cv.size()];
        Log.d(LOG_TAG,"SIZE OF cvArray"+cvArray.length);
        cv.toArray(cvArray);
        return cvArray;
    }
    public static class NewsUpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}