package com.professional.anubhavshankar.newsfeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Anubhav Shankar on 8/30/2016.
 */
public class Utility {
    public static String getPreferredUrl(Context context){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.category),context.getString(R.string.default_category));

    }
}
