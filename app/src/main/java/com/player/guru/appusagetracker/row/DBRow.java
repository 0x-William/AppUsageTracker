package com.player.guru.appusagetracker.row;

import java.util.Date;

/**
 * Created by guru on 12/26/15.
 */
public class DBRow {
    public int id;
    public String user_id;
    public String app_name;
    public String app_package;
    public String location;
    public int duration_day;
    public int day_opens;
    public String date;

    public DBRow(){

    }

    public DBRow(int id, String user_id, String app_name, String app_package, String location, int duration_day, int day_opens, String date){
        this.id = id;
        this.user_id = user_id;
        this.app_name = app_name;
        this.app_package = app_package;
        this.location = location;
        this.duration_day = duration_day;
        this.day_opens = day_opens;
        this.date = date;
    }

    public DBRow(int id, String user_id, String app_name, String app_package, String location, int duration_day, int day_opens, Date date) {
        this.id = id;
        this.user_id = user_id;
        this.app_name = app_name;
        this.app_package = app_package;
        this.location = location;
        this.duration_day = duration_day;
        this.day_opens = day_opens;
        this.date = date.toString();
    }
}
