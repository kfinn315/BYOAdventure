package com.bingcrowsby.byoadventure.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bingcrowsby.byoadventure.Controller.MapConfig;
import com.bingcrowsby.byoadventure.Controller.UserConfig;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kevinfinn on 2/25/15.
 */
public class MySharedPrefs {
    private static final String tag = "sharedPrefs";
    private static final String SP_MAP = "MAP";
    private static final String SP_USER = "USER";
    private static final String SP_ADV = "ADV";


    public static boolean saveMapConfig(Context context, double lat, double lng, float zoom, float bearing, float tilt) {
        Log.i(tag, "save map config");
        SharedPreferences settings = context.getSharedPreferences(SP_MAP, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("longitude", (float)lng);
        editor.putFloat("latitude",  (float)lat);
        editor.putFloat("zoom", zoom);
        editor.putFloat("bearing", bearing);
        editor.putFloat("tilt", tilt);

        return editor.commit();
    }

    public static boolean saveMapConfig(Context context, MapConfig mapConfig){
        return MySharedPrefs.saveMapConfig(context, mapConfig.mtarget.latitude,mapConfig.mtarget.longitude, mapConfig.mzoom, mapConfig.mbearing, mapConfig.mtilt);

    }
    public static MapConfig getMapConfig(Context context) {
        Log.i(tag, "get map config");
        SharedPreferences settings = context.getSharedPreferences("MAP", 0);
        float longitude = settings.getFloat("longitude", 0);
        float latitude = settings.getFloat("latitude", 0);
        LatLng target = new LatLng(latitude, longitude); //with longitude and latitude
        float zoom = settings.getFloat("zoom", 0);
        float bearing = settings.getFloat("bearing", 0);
        float tilt = settings.getFloat("tilt", 0);
        return new MapConfig(target, zoom, bearing, tilt);
    }

    public static boolean saveUserConfig(Context context, UserConfig userConfig){
        Log.i(tag, "save user config");
        SharedPreferences userPrefs = context.getSharedPreferences(SP_USER, 0);
        return userPrefs.edit().putInt("health",userConfig.health).putInt("lives",userConfig.lives).commit();
    }

    public static UserConfig getUserConfig(Context context){
        Log.i(tag, "get user config");
        SharedPreferences userPrefs = context.getSharedPreferences(SP_USER, 0);
        int health = userPrefs.getInt("health",0);
        int lives = userPrefs.getInt("lives",0);
        return new UserConfig(health,lives);

    }

    public static boolean saveAdv(Context context, int index){
        Log.i(tag, "save adventure_index " + index);
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_ADV, 0).edit();
        editor.putInt("adventure_index", index);
        return editor.commit();
    }

    public static int getAdv(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_ADV, 0);
        int id = sharedPreferences.getInt("adventure_index",-1);
        Log.i(tag, "get adventure_index "+id);
        return id;
    }

    public static long getLastOpened(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER, 0);
        long dateLast = sharedPreferences.getLong("last_date",-1);
        return dateLast;
    }

    public static boolean saveLastOpened(Context context, long dateLast){
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_USER, 0).edit();
        editor.putLong("last_date",dateLast);
        return editor.commit();
    }

}
