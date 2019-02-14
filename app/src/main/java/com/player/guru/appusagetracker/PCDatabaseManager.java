package com.player.guru.appusagetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import android.util.Log;

import com.mysql.jdbc.StringUtils;
import com.player.guru.appusagetracker.row.DBRow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by guru on 12/26/15.
 */
public class PCDatabaseManager {
    private static PCDatabaseManager instance;
    private SQLiteDatabase db;

    private PCDatabaseManager(Context context) {
        disconnect();
        connect(context);
    }

    private static synchronized PCDatabaseManager createInstance(Context context) {
        instance = new PCDatabaseManager(context);
        return instance;
    }

    public static PCDatabaseManager getInstance(Context context) {
        PCDatabaseManager localInstance = instance;
        if (localInstance == null) {
            synchronized (PCDatabaseManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = createInstance(context);
                }
            }
        }
        return localInstance;
    }

    public synchronized boolean connect(Context context) {
        if (db != null) {
            return true;
        }
        try {
            db = new PCDatabaseHelper(context).getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            db = null;
        }
        if (db != null)
            return true;

        return false;
    }

    public synchronized void disconnect() {

        // TODO just for test
        // if (db == null)
        // return;
        // if (!db.isOpen()) {
        // db = null;
        // return;
        // }
        // db.close();
        // db = null;
    }

    public synchronized int getCount(String table) {
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
        try {
            if (c.getCount() == 0 || !c.moveToFirst()) {
                return 0;
            }
            int count = c.getInt(0);
            Log.d(DBSettings.DB_LOG, "DB " + table + " count = " + count);
            return count;
        } finally {
            c.close();
        }
    }

    private synchronized void beginTransaction() {
        db.beginTransaction();
    }

    private synchronized void finishTransaction() {
        db.setTransactionSuccessful();
        db.endTransaction();
    }

//    private static final String INSERT = "insert into " + DBSettings.TABLE_USAGE + " ("
//            + DBSettings.COLUMN_USER_ID + ", "
//            + DBSettings.COLUMN_APP_NAME + ", " + DBSettings.COLUMN_APP_PACKAGE + ", "
//            + DBSettings.COLUMN_LOCATION + ", " + DBSettings.COLUMN_OPENED_AT + ", " + DBSettings.COLUMN_CLOSED_AT
//            + ") values ( ?, ?, ?, ?, ?, ?, ? )";

    public synchronized void setSynced(){
        beginTransaction();

        ContentValues cv = new ContentValues();
        cv.put(DBSettings.COLUMN_SYNC, 1);

//        db.update(DBSettings.TABLE_USAGE, cv, DBSettings.COLUMN_ID + " in (" + TextUtils.join(",", ids) + ")", null);
        db.update(DBSettings.TABLE_USAGE, cv, "1", null);

        finishTransaction();
    }

    public synchronized void updateRow (DBRow row){
        beginTransaction();

        DBRow existingRow = getRow(row);

        if (existingRow == null){
            ContentValues cv = new ContentValues();
            cv.put(DBSettings.COLUMN_USER_ID, row.user_id);
            cv.put(DBSettings.COLUMN_APP_NAME, row.app_name);
            cv.put(DBSettings.COLUMN_APP_PACKAGE, row.app_package);
            cv.put(DBSettings.COLUMN_LOCATION, row.location);
            cv.put(DBSettings.COLUMN_DURATION_DAY, row.duration_day);
            cv.put(DBSettings.COLUMN_DAY_OPENS, 1);
            cv.put(DBSettings.COLUMN_DATE, row.date);
            db.insertWithOnConflict(DBSettings.TABLE_USAGE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);

            Log.d("****** new row", row.app_package + ", " + row.duration_day);
        }
        else {
            ContentValues cv = new ContentValues();
            Log.d("****** row updated", row.app_package + ", " + existingRow.duration_day + "+"+ row.duration_day+" = "+(existingRow.duration_day + row.duration_day));
            cv.put(DBSettings.COLUMN_DURATION_DAY, existingRow.duration_day + row.duration_day);
            cv.put(DBSettings.COLUMN_DAY_OPENS, existingRow.day_opens + 1);
            cv.put(DBSettings.COLUMN_SYNC, 0);

            db.update(DBSettings.TABLE_USAGE, cv, DBSettings.COLUMN_ID + "=" + existingRow.id,
                    null);
        }

        finishTransaction();
    }

    public synchronized DBRow getRow(DBRow row){
        Cursor cursor = db.query(DBSettings.TABLE_USAGE, null, DBSettings.COLUMN_APP_PACKAGE+"=? AND "+DBSettings.COLUMN_LOCATION+"=? AND "+DBSettings.COLUMN_DATE+"=?",
                new String[]{row.app_package, row.location, row.date}, null, null, null);

        if (cursor.getCount() < 1 || !cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        return new DBRow(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                    cursor.getInt(5), cursor.getInt(6), cursor.getString(7));
    }

    public synchronized ArrayList<DBRow> getUnsyncedEntries() {
        Cursor cursor = db.query(DBSettings.TABLE_USAGE, null, DBSettings.COLUMN_SYNC + "=0", null, null, null, null);

        ArrayList<DBRow> entries = new ArrayList<DBRow>();

        if (cursor.getCount() < 1 || !cursor.moveToFirst()) {
            cursor.close();
            return entries;
        }

        while (!cursor.isAfterLast()) {
            entries.add(new DBRow(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                    cursor.getInt(5), cursor.getInt(6), cursor.getString(7)));
            cursor.moveToNext();
        }
        cursor.close();

        return entries;
    }

    public synchronized void removeAllApps(){
        beginTransaction();
        db.delete(DBSettings.TABLE_USAGE, "1", null);
        finishTransaction();
    }

}
