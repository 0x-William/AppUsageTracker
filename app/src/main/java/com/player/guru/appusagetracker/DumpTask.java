package com.player.guru.appusagetracker;

import android.os.AsyncTask;
import android.util.Log;

import com.player.guru.appusagetracker.row.DBRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Created by guru on 12/26/15.
 */
public class DumpTask extends AsyncTask<DBRow, Void, Boolean> {
    public interface Callback{
        void onTaskCompleted(Boolean success);
    }

    private Callback callback;

    public DumpTask(Callback callback){
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(DBRow... params) {
        Connection connect = null;
        Statement statement = null;
        PreparedStatement preparedStatement = null;

        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://mysql.freshapp22.org:3306/dutchess", "aceventura", "freshpie22");

            for (DBRow row : params) {
                // PreparedStatements can use variables and are more efficient
                preparedStatement = connect
                        .prepareStatement("insert into " + DBSettings.TABLE_USAGE + " values (?, ?, ?, ?, ?, ?, ?, ?) on duplicate key update " +
                                DBSettings.COLUMN_DURATION_DAY + "=?, " + DBSettings.COLUMN_DAY_OPENS + "=?");

                preparedStatement.setInt(1, row.id);
                preparedStatement.setString(2, row.user_id);
                preparedStatement.setString(3, row.app_name);
                preparedStatement.setString(4, row.app_package);
                preparedStatement.setString(5, row.location);
                preparedStatement.setInt(6, row.duration_day);
                preparedStatement.setInt(7, row.day_opens);
                preparedStatement.setString(8, row.date);

                preparedStatement.setInt(9, row.duration_day);
                preparedStatement.setInt(10, row.day_opens);
                preparedStatement.executeUpdate();
            }

            connect.close();
        } catch (Exception e) {
            Log.d("REMOTE DB SYNC", "Failed");
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);

        callback.onTaskCompleted(aVoid);
    }
}
