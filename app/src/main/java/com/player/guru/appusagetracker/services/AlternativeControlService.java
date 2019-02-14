package com.player.guru.appusagetracker.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import com.mysql.jdbc.Util;
import com.player.guru.appusagetracker.AppUsageTimerTask;
import com.player.guru.appusagetracker.DumpTask;
import com.player.guru.appusagetracker.MyApplication;
import com.player.guru.appusagetracker.PCDatabaseManager;
import com.player.guru.appusagetracker.StopTimerWhenScreenOff;
import com.player.guru.appusagetracker.Utility;
import com.player.guru.appusagetracker.row.DBRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.provider.Settings.Secure;
import android.widget.Toast;

public class AlternativeControlService extends Service implements DumpTask.Callback, LocationListener {
    static public boolean RUN = false;
    static public String currentLauncherPackage;

    public static String preUsedApp = "";
    public static String preUsedPackage = "";

    public static String user_id = "";
    public static String location = "";
    public static Location geolocation = null;
    public static Date last_opened_at;
    public static int seconds = 0;

    private static Context context;
    private static DumpTask.Callback callback;

    private BroadcastReceiver broadcastReceiver;
    private boolean regRec = false;

    public static final String STOP_SERVICE = "STOP_SERVICE";

    private static Timer appUsageTimer;
    private static AppUsageTimerTask appUsageTimerTask;

    private static String TAG = "AlternativeControlService";

    protected static LocationManager locationManager = null;
    private static final float MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 1;
    private static String provider = LocationManager.NETWORK_PROVIDER;

    public static StopTimerWhenScreenOff listner = new StopTimerWhenScreenOff() {

        @Override
        public void stopTimers() {
            Log.d(TAG, "screen off");
            preUsedPackage = "";
            stopTimeTraking();
        }
    };

    public AlternativeControlService() {

    }

    @Override
    public void onCreate(){
        super.onCreate();

        context = this;
        callback = this;
        RUN = true;

        user_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);

        Criteria criteria = new Criteria();

        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled)
            provider = LocationManager.NETWORK_PROVIDER;
        else if (isGPSEnabled)
            provider = locationManager.GPS_PROVIDER;

        try {
            locationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
        }catch (Exception e){

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            if (intent.getBooleanExtra(STOP_SERVICE, false)) {
                Log.d(TAG, "stop with intent");
                stopAlternativeControlService();
                if (broadcastReceiver != null && regRec) {
                    unregisterReceiver(broadcastReceiver);
                    regRec = false;
                }
                return START_STICKY;
            }

        broadcastReceiver = new ScreenOffOnReceiver();
        IntentFilter action = new IntentFilter();
        action.addAction(Intent.ACTION_SCREEN_ON);
        action.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(broadcastReceiver, action);
        regRec = true;
        Intent intentBCSend = new Intent(this, ScreenOffOnReceiver.class);
        intentBCSend.putExtra(ScreenOffOnReceiver.INIT, true);
        sendBroadcast(intentBCSend);

        return START_STICKY;
    }

    public static void timeUsageTracking(final String currentPackage) {

        if (preUsedPackage.equals("")) {
            preUsedPackage = currentPackage;
            preUsedApp = getAppName(currentPackage);
            last_opened_at = new Date();

            return;
        }

        seconds++;

        if (currentPackage.equalsIgnoreCase(preUsedPackage)){
            return;
        }

        Date now = new Date();
        PCDatabaseManager dbManager = PCDatabaseManager.getInstance(context);

        if (!Utility.getTriedDate(context).equals(Utility.dateToString(now))){
            Utility.setTriedDate(context);

            ArrayList rows = dbManager.getUnsyncedEntries();

            if (rows.size() > 0) {
                DumpTask task = new DumpTask(callback);
                DBRow[] rowsArg = (DBRow[]) rows.toArray(new DBRow[rows.size()]);
                task.execute(rowsArg);
            }
        }


        final String currentApp = getAppName(currentPackage);

        /*
        String regex = ".*[.].*[.].*launcher.*";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(preUsedPackage);
        boolean matches = matcher.matches();
*/
        boolean matches = false;
        if (!matches) {

            String dateStr = Utility.dateToString(last_opened_at);

            if (locationManager != null) {
                if (locationManager.isProviderEnabled(provider)) {
                    try{
                        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
                        geolocation = lastKnownLocation;
                        if (lastKnownLocation != null) {
                            location = getLocation(lastKnownLocation);
                        }
                    }
                    catch (Exception e){
                        Log.e("**** Geolocation failed", e.toString());
                    }
                }
            }

            dbManager.updateRow(new DBRow(0, user_id, preUsedApp, preUsedPackage, location, seconds, 1, dateStr));
        }

        seconds = 0;
        last_opened_at = now;
        preUsedApp = currentApp;
        preUsedPackage = currentPackage;

        /*
        preUsedPackage = currentPackage;

        stopTimeTraking();

        appUsageTimerTask = new AppUsageTimerTask(context, currentPackage);

        appUsageTimer = new Timer();
        appUsageTimer.schedule(appUsageTimerTask, 1000, 1000 * 10);
        */
    }

    private static String getAppName(String packageName){
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo (packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    private static void stopTimeTraking() {
        /*
        Log.d(TAG, "stop app usage dailyUsageLimitTimer");
        if (appUsageTimer != null) {
            appUsageTimer.cancel();
            appUsageTimer = null;
        }

        if (appUsageTimerTask != null) {
            appUsageTimerTask.cancel();
            appUsageTimerTask = null;
        }
        */
    }

    private void stopAlternativeControlService() {
/*
        stopTimeTraking();

        Intent intentStop = new Intent(this, ScreenOffOnReceiver.class);
        intentStop.putExtra(ScreenOffOnReceiver.STOP_ALARM_MANAGER, true);
        sendBroadcast(intentStop);

        Log.d(TAG, "stop");

        new Thread(new Runnable() {

            @Override
            public void run() {
                stopSelf();
            }
        }).start();
*/
    }

    @Override
    public void onTaskCompleted(Boolean success) {
        if (success){
            PCDatabaseManager dbManager = PCDatabaseManager.getInstance(context);
            dbManager.setSynced();
            Utility.setSuccessfulDate(context);
        }
    }

    private static String getLocation(Location l){
        if (l == null) return "";

        double latitude = l.getLatitude();
        double longitude = l.getLongitude();
        return Utility.getAddressFromLocation(latitude, longitude, context);
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        AlternativeControlService.geolocation = location;
        AlternativeControlService.location = getLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
