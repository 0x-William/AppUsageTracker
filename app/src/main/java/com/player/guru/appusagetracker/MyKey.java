package com.player.guru.appusagetracker;

/**
 * Created by guru on 1/12/16.
 */
public class MyKey {
    public String app_package;
    public String location;
    public String date;

    @Override
    public int hashCode() {
        String str = app_package + "," + location + "," + date;

        return str.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if(o==this) return true;
        if(o==null || !(o instanceof MyKey)) return false;

        MyKey cp = MyKey.class.cast(o);

        return app_package.equals(cp.app_package) && location.equals(cp.location) && date.equals(cp.date);
    }
}
