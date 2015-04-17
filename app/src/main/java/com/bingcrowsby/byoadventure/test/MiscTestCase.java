package com.bingcrowsby.byoadventure.test;

import android.test.AndroidTestCase;

import com.bingcrowsby.byoadventure.Controller.MapConfig;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
* Created by kevinfinn on 2/17/15.
*/
public class MiscTestCase extends AndroidTestCase {

    public MiscTestCase() {
        super();
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

//        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());


    }

    public void testMapConfig(){
        MapConfig mapConfig = new MapConfig(null,0,0,0);
        try {
            CameraPosition position = mapConfig.getCameraPosition();
            assert(false);
        } catch(NullPointerException e){
            assert(true);
        }


        mapConfig = new MapConfig(new LatLng(0,1),2,3,4);
        assertMapConfigEqualsCameraPos(mapConfig, mapConfig.getCameraPosition());

//        assertMapConfigEqualsCameraPos(new MapConfig(new LatLng(-1000,10000.0),0,0,4444444), mapConfig.getCameraPosition());
    }

    private void assertMapConfigEqualsCameraPos(MapConfig mapConfig, CameraPosition cameraPosition){

        assertEquals(mapConfig.mbearing, cameraPosition.bearing);
        assertEquals(mapConfig.mtilt,cameraPosition.tilt);
        assertEquals(mapConfig.mtarget,cameraPosition.target);
        assertEquals(mapConfig.mzoom,cameraPosition.zoom);
    }
    @Override
    public void tearDown() throws Exception {
        super.tearDown();

//        solo.finishOpenedActivities();


    }


}
