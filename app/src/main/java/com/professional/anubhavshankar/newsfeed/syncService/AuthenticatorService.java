package com.professional.anubhavshankar.newsfeed.syncService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Anubhav Shankar on 9/13/2016.
 */
public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        mAuthenticator= new Authenticator(this);
    }
}
