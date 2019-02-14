package com.player.guru.appusagetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by guru on 12/26/15.
 */

public class PCDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pc.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_USAGE_TIME = "create table " + DBSettings.TABLE_USAGE + " ("
            + DBSettings.COLUMN_ID + " integer not null PRIMARY KEY AUTOINCREMENT," + DBSettings.COLUMN_USER_ID + " text not null ,"
            + DBSettings.COLUMN_APP_NAME + " text not null ," + DBSettings.COLUMN_APP_PACKAGE + " text not null ,"
            + DBSettings.COLUMN_LOCATION + " text ," + DBSettings.COLUMN_DURATION_DAY + " integer ," + DBSettings.COLUMN_DAY_OPENS + " integer,"
            + DBSettings.COLUMN_DATE + " text ," + DBSettings.COLUMN_SYNC + " integer default 0" + ");";

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(DBSettings.DB_LOG, "onCreate");

        database.execSQL(DATABASE_CREATE_USAGE_TIME);
    }

    public PCDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        Log.d(DBSettings.DB_LOG, "onUpgrade");

        db.execSQL("DROP TABLE IF EXISTS " + DBSettings.TABLE_USAGE);

        onCreate(db);
    }

}