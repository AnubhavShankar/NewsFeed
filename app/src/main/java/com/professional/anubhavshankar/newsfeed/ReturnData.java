package com.professional.anubhavshankar.newsfeed;

import android.database.Cursor;

import com.professional.anubhavshankar.newsfeed.data.NewsFeed;

import java.util.List;

public interface ReturnData{
    void handleData(Cursor myFeed);
}
