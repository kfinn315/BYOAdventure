package com.bingcrowsby.byoadventure;

import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.joda.time.Days;
import org.joda.time.Instant;

import java.text.NumberFormat;

/**
 * Created by kevinfinn on 1/10/15.
 */
public class Statics {

    private static final String tag = "statics";
    private static SupportMapFragment mapFragment;

    public static SupportMapFragment getMapFragment() {
        Log.i(tag, "get map frag");
        if (mapFragment == null)
            Statics.mapFragment = SupportMapFragment.newInstance();

        return mapFragment;
    }

    public static String formatLocation(LatLng latLng) {

        if(latLng == null) return null;
        double lng = latLng.longitude;
        double lat = latLng.latitude;
        NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(2);
        return "(" + numFormat.format(lat) + "," + numFormat.format(lng) + ")";

    }

    public static String formatTime(int iSeconds) {

        StringBuilder builder = new StringBuilder();
        int seconds, minutes, hours, days;

        seconds = (int) iSeconds % 60;
        minutes = (int) (iSeconds / (60) % 60);
        hours = (int) (iSeconds / (60 * 60) % 24);
        days = (int) (iSeconds / (60 * 60 * 24));
        if (days > 0)
            builder.append(days + "d ");
        if (hours > 0)
            builder.append(hours + "h ");
        if (minutes > 0)
            builder.append(minutes + "m ");
        if (iSeconds > 0)
            builder.append(seconds + "s");

        return builder.toString();
    }

    public static String formatDistance(int meters) {
        float km = ((float) meters) / (1000f);

        float miles = km / (0.621371f);
        NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(1);

        return String.valueOf(numFormat.format(miles)) + " miles";
    }

    public static long getTimeIntervalInDays(long start, long end){
        return Days.daysBetween(new Instant(start), new Instant(end)).getDays();
    }

    public static double computeLineLength(LatLng start, LatLng end){
        return SphericalUtil.computeDistanceBetween(start, end);
    }
}
