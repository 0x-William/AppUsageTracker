package com.player.guru.appusagetracker;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Set;
import java.util.TimerTask;

/**
 * Created by guru on 12/26/15.
 */
public class AppUsageTimerTask extends TimerTask {
    private String TAG = "AppUsageTimerTask";
    private Context context;
    private String appPackage;

    public AppUsageTimerTask(Context context, String appPackage) {
        this.context = context;
        this.appPackage = appPackage;
        Log.d(TAG, "track usage time for " + appPackage);
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void run() {
        Log.d(TAG, appPackage + " Is Open");


    }

}
