package com.professional.anubhavshankar.newsfeed;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.professional.anubhavshankar.newsfeed.data.FetchNewsTask;
import com.professional.anubhavshankar.newsfeed.data.NewsCardAdapter;
import com.professional.anubhavshankar.newsfeed.data.NewsFeed;
import com.professional.anubhavshankar.newsfeed.data.NewsFeedContract;
import com.professional.anubhavshankar.newsfeed.service.ServiceIntent;
import com.professional.anubhavshankar.newsfeed.syncService.SyncAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anubhav Shankar on 8/31/2016.
 */
public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int GET_NEWS_FEED=1;
    private static final String[] NEWS_FEED_PROJECTIONS={
            NewsFeedContract.NewsEntry.TABLE_NAME+"."+ NewsFeedContract.NewsEntry._ID,
            NewsFeedContract.NewsEntry.COLUMN_TITLE,
            NewsFeedContract.NewsEntry.COLUMN_STORY_IMAGE,
            NewsFeedContract.NewsEntry.COLUMN_Category,
            NewsFeedContract.NewsEntry.COLUMN_DETAIL,
            NewsFeedContract.NewsEntry.COLUMN_DATE
    };
    public static final int COL_NEWS_ID=0;
    public static final int COL_NEWS_TITLE=1;
    public static final int COL_NEWS_STORY_IMG=2;
    public static final int COL_NEWS_CATEGORY=3;
    public static final int COL_NEWS_DETAIL=4;
    public static final int COL_NEWS_DATE=5;
    RecyclerView recyclerView;
    public ArrayList<NewsFeed> newsFeeds;
    public NewsCardAdapter newsCardAdapter;
    private final String LOG_TAG=NewsFragment.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(GET_NEWS_FEED, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.news_list, container,false);
        recyclerView=(RecyclerView)view.findViewById(R.id.news_list);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        Log.d(LOG_TAG, "Fragment onCreateView");
        //Log.d("MainActivity","getting inside2: "+newsFeeds.size());
        newsCardAdapter= new NewsCardAdapter(getContext(),null);
        recyclerView.setAdapter(newsCardAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        /* IntentService Approach
        String url=Utility.getPreferredUrl(getContext());
        Intent intent= new Intent(getContext(), ServiceIntent.class);
        intent.putExtra("url",url);
        getContext().startService(intent);
        */
        //new FetchNewsTask(getContext()).execute(url); // asyncTaskApproach
        SyncAdapter.syncImmediately(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder= NewsFeedContract.NewsEntry.COLUMN_DATE+" DESC";
        return new CursorLoader(getActivity(), NewsFeedContract.NewsEntry.CONTENT_URI,NEWS_FEED_PROJECTIONS,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, Integer.toString(data.getCount()));
        newsCardAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        newsCardAdapter.swapCursor(null);
    }
}
