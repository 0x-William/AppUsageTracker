package com.player.guru.appusagetracker.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.player.guru.appusagetracker.AppDetectionTimerTask;
import com.player.guru.appusagetracker.StopTimerWhenScreenOff;

import java.util.Timer;

public class ScreenOffOnReceiver extends BroadcastReceiver {

    public static final String STOP_ALARM_MANAGER = "STOP_ALARM_MANAGER";
    public static final String INIT = "INIT";

    private StopTimerWhenScreenOff listner;
    private Timer timer;
    private AppDetectionTimerTask timerTask;

    @Override
    public void onReceive(Context context, Intent intent) {

        listner = AlternativeControlService.listner;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, new Intent(context, ActivityTopReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT | Intent.FILL_IN_DATA);

        if (intent.getBooleanExtra(INIT, false)) {

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)    // greater than 20
                startDetectionTimer(context);
            else
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1 * 1000, 1000, pendingIntent);
        }

        if (intent.getAction() != null) {
            if (intent.getAction().toString().equalsIgnoreCase(Intent.ACTION_SCREEN_ON)) {
                Log.d("TEST", "ScreenOn");
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)    // greater than 20
                    startDetectionTimer(context);
                else
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1 * 1000, 1000, pendingIntent);

            } else if (intent.getAction().toString().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
                Log.d("TEST", "ScreenOff");
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)    // greater than 20
                    cancelDetectionTimer(context);
                else
                    alarmManager.cancel(pendingIntent);
                listner.stopTimers();
            } else {
                Log.d("TEST", "else ScreenOffOnReceiver");
            }
        }

        if (intent.getBooleanExtra(STOP_ALARM_MANAGER, false)) {
            Log.d("LOG", "STOP_ALARM_MANAGER RECEIVER ");
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)    // greater than 20
                cancelDetectionTimer(context);
            else
                alarmManager.cancel(pendingIntent);
            listner.stopTimers();
        }
    }

    private void startDetectionTimer(Context context)
    {
        if (timerTask == null) {
            timer = new Timer();
            timerTask = new AppDetectionTimerTask(context);
            timer.schedule(timerTask, 1000, 1000 * 1);
        }
    }

    private void cancelDetectionTimer(Context context)
    {
        if(timer != null)
            timer.cancel();;
        if(timerTask != null)
            timerTask.cancel();
        timer = null;
        timerTask = null;
    }
}