package com.player.guru.appusagetracker.services;

/**
 * Created by guru on 12/26/15.
 */

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.player.guru.appusagetracker.MyApplication;
import com.player.guru.appusagetracker.PCDatabaseManager;


public class ActivityTopReceiver extends BroadcastReceiver {

    private String currentLauncherPackage;
    private String preUsedPackage;
    private Context context;

    @Override
    public void onReceive(Context context, Intent arg1) {

        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (AlternativeControlService.RUN) {
            String currentPackage;

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){    // greater than 20
                currentPackage = manager.getRunningAppProcesses().get(0).processName;
            }
            else {
                List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
                ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
                currentPackage = componentInfo.getPackageName();
            }

            AlternativeControlService.timeUsageTracking(currentPackage);
        }
    }

}