package com.bingcrowsby.byoadventure.test;

import android.content.Context;
import android.test.AndroidTestCase;

import com.bingcrowsby.byoadventure.Controller.MapConfig;
import com.bingcrowsby.byoadventure.Controller.UserConfig;
import com.bingcrowsby.byoadventure.Model.MySharedPrefs;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by kevinfinn on 2/17/15.
 */
public class MySharedPrefsTestCase extends AndroidTestCase {
    Context context;

    public MySharedPrefsTestCase() {
        super();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        context = getContext();
    }

    public void testMapConfig(){
        LatLng latLng = new LatLng(0.0,0.1);
        float zoom = 0f, bearing = 0.1f, tilt = 0.2f;
        MapConfig mapConfig = new MapConfig(latLng,zoom,bearing,tilt);
        MySharedPrefs.saveMapConfig(context,latLng.latitude,latLng.longitude,zoom,bearing,tilt);
        MapConfig actualConfig = MySharedPrefs.getMapConfig(context);
        assertEquals(mapConfig.mbearing,actualConfig.mbearing);
        assertLatLngEquals(mapConfig.mtarget, actualConfig.mtarget);
        assertEquals(mapConfig.mtilt, actualConfig.mtilt);
        assertEquals(mapConfig.mzoom, actualConfig.mzoom);
    }

    public void assertLatLngEquals(LatLng a, LatLng b){
        assertEquals(a.latitude, b.latitude, .0001);
        assertEquals(a.longitude, b.longitude, .0001);
    }

    public void testUserConfig(){
        int lives = 5;
        int health = 5;
        UserConfig userConfig = new UserConfig(lives,health);

        MySharedPrefs.saveUserConfig(context,userConfig);
        UserConfig userConfig1 = MySharedPrefs.getUserConfig(getContext());
        assertEquals(lives, userConfig1.lives);
        assertEquals(health, userConfig.health);
    }

    public void testAdv(){
        int expectedId = 1003;
        MySharedPrefs.saveAdv(context,expectedId);
        int id = MySharedPrefs.getAdv(context);
        assertEquals(expectedId, id);
    }

    public void testLastDate(){
        Date date = new Date();
        MySharedPrefs.saveLastOpened(context, date.getTime());
        Date actualDate = new Date(MySharedPrefs.getLastOpened(context));
        assertEquals(date,actualDate);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

//        solo.finishOpenedActivities();


    }


}
