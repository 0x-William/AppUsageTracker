package com.player.guru.appusagetracker.row;

/**
 * Created by guru on 2/20/16.
 */
public class StatRow {
    public String app_name;
    public int time_spent;
    public float time_spent_percent;
    public float opens_per_day;

    public StatRow (String app_name, int time_spent, float time_spent_percent, float opens_per_day){
        this.app_name = app_name;
        this.time_spent = time_spent;
        this.time_spent_percent = time_spent_percent;
        this.opens_per_day = opens_per_day;
    }
}
