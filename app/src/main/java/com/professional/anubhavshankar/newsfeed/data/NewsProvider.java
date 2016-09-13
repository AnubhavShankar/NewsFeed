package com.professional.anubhavshankar.newsfeed.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.Preference;
import android.support.v4.view.NestedScrollingChild;
import android.util.Log;

/**
 * Created by Anubhav Shankar on 8/30/2016.
 */
public class NewsProvider extends ContentProvider {

    private static final int NEWS=100;
    private static final int NEWS_WITH_ID=200;
    private static UriMatcher sUriMatcher=buildUriMatcher();
    private static NewsFeedDbHelper mNewsDbHelper;

    private static final String LOG_TAG=NewsProvider.class.getSimpleName();
    public static UriMatcher buildUriMatcher(){
        UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        String authority=NewsFeedContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,NewsFeedContract.PATH_NEWS,NEWS);
        matcher.addURI(authority,NewsFeedContract.PATH_NEWS+"/#",NEWS_WITH_ID);
        return matcher;
    }
    @Override
    public boolean onCreate() {
        mNewsDbHelper=new NewsFeedDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match= sUriMatcher.match(uri);
        Cursor retCursor=null;
        switch (match) {
            case NEWS: {
                Log.d(LOG_TAG,"ALL News Query detected");
                retCursor = mNewsDbHelper.getReadableDatabase().query(
                        NewsFeedContract.NewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case NEWS_WITH_ID:
                Log.d(LOG_TAG,"SINGLE News Query detected");
                mNewsDbHelper.getReadableDatabase().query(
                        NewsFeedContract.NewsEntry.TABLE_NAME,
                        projection,
                        NewsFeedContract.NewsEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        Log.d(LOG_TAG,Integer.toString(retCursor.getCount()));
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        int match=sUriMatcher.match(uri);
        switch (match){
            case NEWS_WITH_ID:
                Log.d(LOG_TAG,"Single News Query TYPE detected");
                return NewsFeedContract.NewsEntry.CONTENT_ITEM_TYPE;
            case NEWS:
                Log.d(LOG_TAG,"ALL News Query TYPE detected");
                return NewsFeedContract.NewsEntry.CONTENT_TYPE;
            default:
                Log.d(LOG_TAG,"Unknown TYPE detected");
                throw new UnsupportedOperationException("UNKNOWN URI: "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match=sUriMatcher.match(uri);
        final SQLiteDatabase db=mNewsDbHelper.getWritableDatabase();
        Uri returnUri=null;
        switch (match){
            case NEWS: {
                long id=db.insert(NewsFeedContract.NewsEntry.TABLE_NAME,null,values);

                if(id>0)
                    returnUri=ContentUris.withAppendedId(uri,id);
                else
                    throw new android.database.SQLException("Failed to insert row into database for URI: "+uri);
                break;
            }
            default: throw new UnsupportedOperationException("UNKNOWN URI FOR INSERT:"+ uri);

        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match=sUriMatcher.match(uri);
        SQLiteDatabase db=mNewsDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (match){
            case NEWS:
                rowsDeleted=db.delete(NewsFeedContract.NewsEntry.TABLE_NAME,selection,
                        selectionArgs);
                break;
            default: throw new UnsupportedOperationException("Unknown Uri: "+uri);
        }
        if(rowsDeleted<0|| selection==null)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match=sUriMatcher.match(uri);
        int rowsUpdated;
        SQLiteDatabase db=mNewsDbHelper.getWritableDatabase();
        switch(match){
            case NEWS:
                rowsUpdated=db.update(NewsFeedContract.NewsEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("UNKNOWN URI: "+uri);
        }
        if (rowsUpdated!=0)
            getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db=mNewsDbHelper.getWritableDatabase();
        final int match=sUriMatcher.match(uri);
        switch (match){
            case NEWS:
                db.beginTransaction();
                int returnCount=0;
                try{
                    for(ContentValues value:values){
                        long id=db.insert(NewsFeedContract.NewsEntry.TABLE_NAME,null,value);
                        if(id!=-1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                if (returnCount>0)
                    getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }

    }
}
