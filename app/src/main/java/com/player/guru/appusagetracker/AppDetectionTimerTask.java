package com.player.guru.appusagetracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.player.guru.appusagetracker.services.ActivityTopReceiver;

import java.util.TimerTask;

/**
 * Created by guru on 12/26/15.
 */
public class AppDetectionTimerTask extends TimerTask {
    private String TAG = "AppUsageTimerTask";
    private Context context;
    public AppDetectionTimerTask(Context context) {
        this.context = context;
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void run() {
//        Log.d("**************MyDebug", "AppDetectionTimerTask");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, new Intent(context, ActivityTopReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);


        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
