package com.player.guru.appusagetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by guru on 1/14/16.
 */
public class Utility {
    public static final String dateFormat = "MMM dd, yyyy";
    public static final String key = "LastSyncedDate";

    public static String getSuccessfulDate(Context context){
        SharedPreferences prefs = context.getSharedPreferences(key, 0);
        return prefs.getString("successful", "");
    }

    public static void setSuccessfulDate(Context context){
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(key, 0).edit();
        Date now = new Date();
        prefsEditor.putString("successful", dateToString(now));

        prefsEditor.commit();
    }

    public static String getTriedDate(Context context){
        SharedPreferences prefs = context.getSharedPreferences(key, 0);
        return prefs.getString("tried", "");
    }

    public static void setTriedDate(Context context){
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(key, 0).edit();
        Date now = new Date();
        prefsEditor.putString("tried", dateToString(now));

        prefsEditor.commit();
    }

    public static String dateToString(Date date){
        DateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        return format.format(date);
    }

    public static Date stringToDate(String str){
        DateFormat format = new SimpleDateFormat(dateFormat, Locale.ENGLISH);

        try {
            Date date = format.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);

//                return address.getThoroughfare() + ", " + address.getLocality();
                return address.getLocality();
            }
        } catch (IOException e) {
            Log.e("Geocoding", "Unable connect to Geocoder", e);
        } finally {

        }

        return result;
    }

    public static String stringFromSeconds(int seconds) {
        int n = seconds / 60;

        if (n >= 60 * 100) {
            double days = n / (60.0 * 24);
            NumberFormat formatter = new DecimalFormat("#0.0");

            return formatter.format(days) + " days";
        }
        if (n >= 60) {
            return (n * 100 / 60) / 100.0 + " hrs";
        }

        return n + " min";
    }
}
