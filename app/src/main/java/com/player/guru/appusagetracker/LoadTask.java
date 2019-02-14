package com.player.guru.appusagetracker;

import android.os.AsyncTask;
import android.util.Log;

import com.player.guru.appusagetracker.row.StatRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by guru on 12/26/15.
 */
public class LoadTask extends AsyncTask<Void, Void, ArrayList<StatRow>> {
    public interface Callback{
        void onLoadCompleted(int nDays, ArrayList<StatRow> statRows);
    }

    private Callback callback;
    private String user_id;
    private int nDays;

    public LoadTask(String user_id, int nDays, Callback callback){
        this.user_id = user_id;
//        this.user_id = "4f4174bd017a76aa";
        this.nDays = nDays;
        this.callback = callback;
    }

    @Override
    protected ArrayList<StatRow> doInBackground(Void... params) {
        Connection connect = null;
        Statement statement = null;
        ArrayList<StatRow> statRows = new ArrayList<>();

        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://mysql.freshapp22.org:3306/dutchess", "aceventura", "freshpie22");

            // Statements allow to issue SQL queries to the database
            statement = connect.createStatement();

            String totalQuery;
            String fetchQuery;
            if (nDays == 0) {
                fetchQuery = "SELECT `app_name`, sum(`duration_day`) as DURATION, avg(`day_opens`) FROM " + DBSettings.TABLE_USAGE + " where `user_id`='" + user_id +
                        "' and `app_name`!='home' group by `app_name` order by `DURATION` desc";
                totalQuery = "SELECT sum(`duration_day`) FROM " + DBSettings.TABLE_USAGE + " where `user_id`='" + user_id +
                        "' and `app_name`!='home'";
            }
            else {
                String nowStr = Utility.dateToString(new Date());
                fetchQuery = "SELECT `app_name`, sum(`duration_day`) as DURATION, avg(`day_opens`) FROM " + DBSettings.TABLE_USAGE + " where `user_id`='" + user_id +
                        "' and `app_name`!='home' and STR_TO_DATE(date, '%M %d,%Y') > STR_TO_DATE('" + nowStr + "', '%M %d,%Y')-" + nDays + " group by `app_name` order by `DURATION` desc";
                totalQuery = "SELECT sum(`duration_day`) FROM " + DBSettings.TABLE_USAGE + " where `user_id`='" + user_id +
                        "' and `app_name`!='home' and STR_TO_DATE(date, '%M %d,%Y') > STR_TO_DATE('" + nowStr + "', '%M %d,%Y')-" + nDays;
            }

            ResultSet totalSet = statement.executeQuery(totalQuery);
            int sum = 1;
            if (totalSet.next()) {
                sum = totalSet.getInt(1);
            }

            ResultSet result = statement.executeQuery(fetchQuery);
            while (result.next()) {
                StatRow row = new StatRow(
                        result.getString(1),
                        result.getInt(2),
                        result.getInt(2) * 100.0f / sum,
                        result.getFloat(3)
                );
                statRows.add(row);
            }
            statRows.add(new StatRow(
                    "Total",
                    sum,
                    100,
                    1
            ));

            connect.close();
        } catch (Exception e) {
            Log.d("REMOTE DB LOAD", "Failed");
            return null;
        }

        return statRows;
    }

    @Override
    protected void onPostExecute(ArrayList<StatRow> statRows) {
        super.onPostExecute(statRows);

        callback.onLoadCompleted(nDays, statRows);
    }
}
