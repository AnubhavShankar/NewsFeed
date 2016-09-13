package com.professional.anubhavshankar.newsfeed.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.professional.anubhavshankar.newsfeed.data.NewsFeedContract.*;

/**
 * Created by Anubhav Shankar on 8/30/2016.
 */
public class NewsFeedDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="newsfeed.db";
    public static final int DATABASE_VERSION=2;

    public NewsFeedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE_NEWS="CREATE TABLE "+ NewsEntry.TABLE_NAME+"("+
                NewsEntry._ID+" "+"INTEGER PRIMARY KEY AUTOINCREMENT"+","+
                NewsEntry.COLUMN_TITLE+" "+"TEXT NOT NULL"+","+
                NewsEntry.COLUMN_STORY_IMAGE+" "+"TEXT NOT NULL"+","+
                NewsEntry.COLUMN_Category+" "+"TEXT NOT NULL"+","+
                NewsEntry.COLUMN_DETAIL+" "+"TEXT NOT NULL"+","+
                NewsEntry.COLUMN_DATE+" "+"INTEGER NOT NULL"+","+
                "UNIQUE("+NewsEntry.COLUMN_TITLE+","+NewsEntry.COLUMN_DATE+") ON CONFLICT IGNORE"+
                ");";
        db.execSQL(CREATE_TABLE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+NewsEntry.TABLE_NAME);
        onCreate(db);
    }
}
