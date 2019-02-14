package com.player.guru.appusagetracker;

import android.app.Application;
import android.content.Intent;

import com.player.guru.appusagetracker.services.AlternativeControlService;

/**
 * Created by guru on 12/26/15.
 */
public class MyApplication extends Application {
    public final static String UNINSTALLER_PACKAGE_NAME = "com.android.packageinstaller";
    public final static String DEVICE_ANDMIN_SCREEN_PACKAGE = "com.android.settings.DeviceAdminAdd";
    public final static String DEVICE_ANDMIN_SCREEN_SETTINGS = "com.android.settings";
    public final static String SYSTEM_UI_PACKAGE_NAME = "com.android.systemui";
    public final static String SYSTEM_PHONE_PACKAGE_NAME = "com.android.phone";
    public final static String SYSTEM_DIALER_PACKAGE_NAME = "com.android.dialer";
    public final static String SYSTEM_CONTACTS_PACKAGE_NAME = "com.android.contacts";
    public final static String SYSTEM_LAUNCHER_PACKAGE_NAME = "com.android.launcher3";

    @Override
    public void onCreate(){
        super.onCreate();

        startAlternativeControlService();
    }

    private void startAlternativeControlService() {

        Intent intent = new Intent(this, AlternativeControlService.class);
        startService(intent);

    }
}
