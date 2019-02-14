package com.player.guru.appusagetracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.player.guru.appusagetracker.adapter.StatAdapter;
import com.player.guru.appusagetracker.row.DBRow;
import com.player.guru.appusagetracker.row.StatRow;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DumpTask.Callback, LoadTask.Callback {
    private enum Mode {
        Today,
        Week,
        All
    }
    ProgressDialog mDialog;
    TextView versionTextview;
    TextView descTextview;
    ListView appListview;
    Button btnToday;
    Button btnWeek;
    Button btnAll;
    Button btnDump;
    String user_id;
    Mode mode;
    int brown = Color.parseColor("#CE9829");
    int gray = Color.parseColor("#808080");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Dumping...");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        versionTextview = (TextView)findViewById(R.id.version_textview);
        versionTextview.setText("App version : " + BuildConfig.VERSION_NAME);

        btnDump = (Button)findViewById(R.id.dump_btn);
        btnDump.setBackgroundColor(Color.parseColor("#00aa00"));
        btnDump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mDialog.setMessage("Updating...");
            mDialog.show();
            Utility.setTriedDate(MainActivity.this);

            PCDatabaseManager dbManager = PCDatabaseManager.getInstance(getApplicationContext());

            ArrayList rows = dbManager.getUnsyncedEntries();

            DumpTask task = new DumpTask(MainActivity.this);
            DBRow[] rowsArg = (DBRow[]) rows.toArray(new DBRow[rows.size()]);
            task.execute(rowsArg);
            }
        });

        descTextview = (TextView)findViewById(R.id.desc_textview);
        appListview = (ListView)findViewById(R.id.app_listview);
        btnToday = (Button)findViewById(R.id.today_btn);
        btnWeek = (Button)findViewById(R.id.week_btn);
        btnAll = (Button)findViewById(R.id.all_btn);

        btnToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickToday();
            }
        });
        btnWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickWeek();
            }
        });
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAll();
            }
        });

        clickWeek();
    }

    private void loadData(String desc, int nDays) {
        mDialog.setMessage("Loading...");
        mDialog.show();

        descTextview.setText(desc);

        LoadTask task = new LoadTask(user_id, nDays, MainActivity.this);
        task.execute();
        appListview.setTag(task);
    }

    private void clickToday() {
        mode = Mode.Today;

        btnToday.setBackgroundColor(brown);
        btnWeek.setBackgroundColor(gray);
        btnAll.setBackgroundColor(gray);

        Date now = new Date();
        loadData("Today's Summary: " + Utility.dateToString(now), 1);
    }
    private void clickWeek() {
        mode = Mode.Week;

        btnToday.setBackgroundColor(gray);
        btnWeek.setBackgroundColor(brown);
        btnAll.setBackgroundColor(gray);

        Date now = new Date();
        loadData("Last 7 days from " + Utility.dateToString(new Date(now.getTime() - 6 * 24 * 3600 * 1000)), 7);
    }
    private void clickAll() {
        mode = Mode.All;

        btnToday.setBackgroundColor(gray);
        btnWeek.setBackgroundColor(gray);
        btnAll.setBackgroundColor(brown);

        loadData("Total Summary", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        btnDump.setEnabled(false);

        Date now = new Date();
        if (!Utility.getSuccessfulDate(this).equals(Utility.dateToString(now))){
            PCDatabaseManager dbManager = PCDatabaseManager.getInstance(getApplicationContext());
            if (dbManager.getUnsyncedEntries().size() > 0){
                btnDump.setEnabled(true);
            }
        }

        if (mode == Mode.Today) {
            descTextview.setText("Today's Summary: " + Utility.dateToString(now));
        }
        else if (mode == Mode.Week) {
            descTextview.setText("Last 7 days from " + Utility.dateToString(new Date(now.getTime() - 6 * 24 * 3600 * 1000)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskCompleted(Boolean success) {
        if (success){
            PCDatabaseManager dbManager = PCDatabaseManager.getInstance(getApplicationContext());
            dbManager.setSynced();
            Utility.setSuccessfulDate(MainActivity.this);
//            btnDump.setEnabled(false);
        }

        mDialog.hide();

//        Toast.makeText(this, success ? "Successfully Updated" : "Update failed", Toast.LENGTH_LONG).show();

        if (mode == Mode.Today){
            clickToday();
        }
        else if (mode == Mode.Week){
            clickWeek();
        }
        else {
            clickAll();
        }
    }

    @Override
    public void onLoadCompleted(int nDays, ArrayList<StatRow> statRows) {
        if (statRows != null) {
            StatAdapter adapter = new StatAdapter(this, statRows);

            appListview.setAdapter(adapter);
        }

        mDialog.hide();
    }
}
