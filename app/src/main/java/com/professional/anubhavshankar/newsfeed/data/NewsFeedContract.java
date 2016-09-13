package com.professional.anubhavshankar.newsfeed.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Anubhav Shankar on 8/30/2016.
 */
public class NewsFeedContract {
    public static final String CONTENT_AUTHORITY="com.professional.anubhavshankar.newsfeed.app";
    public static final Uri BASE_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_NEWS="news";

    //Corresponding to Table News
    public static class NewsEntry implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_URI.buildUpon().appendPath(PATH_NEWS).build();
        public static final String CONTENT_TYPE="vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_NEWS;
        public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_NEWS;
        public static final String TABLE_NAME="news";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_DATE="updated_at";
        public static final String COLUMN_STORY_IMAGE="story_image";
        public static final String COLUMN_Category="category";
        public static final String COLUMN_DETAIL="detail_link";
        public static final Uri buildNewsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}
