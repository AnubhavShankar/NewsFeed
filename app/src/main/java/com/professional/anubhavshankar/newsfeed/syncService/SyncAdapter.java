package com.professional.anubhavshankar.newsfeed.syncService;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.professional.anubhavshankar.newsfeed.R;
import com.professional.anubhavshankar.newsfeed.Utility;
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
 * Created by Anubhav Shankar on 9/13/2016.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG =SyncAdapter.class.getSimpleName() ;
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection httpURLConnection=null;
        BufferedReader reader=null;
        List<NewsFeed> myNewsFeed=null;
        try{
            String url= Utility.getPreferredUrl(getContext());
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
                Log.d(LOG_TAG,"INPUT STREAM NOT OPENED");
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
        int rows=getContext().getContentResolver().bulkInsert(NewsFeedContract.NewsEntry.CONTENT_URI, cvArray);
        Log.d(LOG_TAG,"Number of rows inserted into database"+rows);

        return;
    }
    public void cleanupDB(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-4);
        Date d=new Date(c.getTimeInMillis());
        Long dateVal=NewsFeed.getDBFormatDate(d);
        String selection= NewsFeedContract.NewsEntry.COLUMN_DATE+ " <= "+dateVal;
        int deletedRows=getContext().getContentResolver().delete(NewsFeedContract.NewsEntry.CONTENT_URI, selection, null);
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
    public static class NewsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}
