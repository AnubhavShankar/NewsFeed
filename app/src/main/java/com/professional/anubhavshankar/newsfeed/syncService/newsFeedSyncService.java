package com.professional.anubhavshankar.newsfeed.syncService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Anubhav Shankar on 9/13/2016.
 */
public class newsFeedSyncService extends Service {
    private static final Object sSyncAdapterLoc=new Object();
    private static final String LOG_TAG =newsFeedSyncService.class.getSimpleName() ;
    private static SyncAdapter sSyncAdapter=null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG,"onCreate syncService method...");
        synchronized (sSyncAdapterLoc){
            if(sSyncAdapter==null){
                sSyncAdapter= new SyncAdapter(getApplicationContext(),true);
            }
        }
    }
}
