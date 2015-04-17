package com.bingcrowsby.byoadventure.test;

import android.app.Instrumentation;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ListView;

import com.bingcrowsby.byoadventure.Activity.MainActivity;
import com.bingcrowsby.byoadventure.Model.AdventureDrawer;
import com.bingcrowsby.byoadventure.Model.AdventureMap;

import com.bingcrowsby.byoadventure.Model.PointList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.robotium.solo.Solo;

/**
* Created by kevinfinn on 2/17/15.
*/
public class AdventureActivityTestCase extends ActivityInstrumentationTestCase2<MainActivity> {
    Instrumentation instrumentation;
    MainActivity mainActivity;
    ListView listView;
    int position;
    Solo solo;

    public AdventureActivityTestCase() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        instrumentation = getInstrumentation();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        solo = new Solo(getInstrumentation(),getActivity());

        mainActivity = getActivity();
    }

    @UiThreadTest
    public void testAdventureDrawer() {
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(MainActivity.class.getName(),null,false);

        final GoogleMap googleMap = mainActivity.mMap;

        final PointList mockPointList = new PointList();
        LatLng pointA = new LatLng(0,0);
        LatLng pointB = new LatLng(1,0);
        mockPointList.add(pointA);
        mockPointList.add(pointB);
        final LatLng mockProgressPoint = mockPointList.getCurrentPoint();

        final AdventureMap mockAdventureMap = new AdventureMap.Builder().linePoints(mockPointList).bounds(AdventureDrawer.getBounds(mockPointList)).build();
        //drawAdventure

        //test for nullpointerexception() when passing in null arguments
        try {
            AdventureDrawer.drawAdventure(null, googleMap, mockAdventureMap);
            assert (false);
        } catch (Exception e) {
            assert (true);
        }
        try {
            AdventureDrawer.drawAdventure(getInstrumentation().getContext(), null, mockAdventureMap);
            assert (false);
        } catch (Exception e) {
            assert (true);
        }
        try {
            AdventureDrawer.drawAdventure(getInstrumentation().getContext(), googleMap, null);
            assert (false);
        } catch (Exception e) {
            assert (true);
        }

        PolylineOptions polylineOptions = AdventureDrawer.createPolylineOptions(mockAdventureMap.getPointList());
        PolylineOptions expectedPolylineOpt = new PolylineOptions().add(pointA).add(pointB).color(Color.BLUE).width(3);
        assert(polylineOptions.equals(expectedPolylineOpt));

        try {
            AdventureDrawer.drawAdventure(getInstrumentation().getContext(), googleMap, mockAdventureMap);
        } catch(Exception e){
            e.printStackTrace();
        }

        AdventureDrawer.drawMarkers(googleMap,mockAdventureMap);
        assertEquals(pointA,mockAdventureMap.getStartMarker().getPosition());
        assertEquals(pointA,mockAdventureMap.getProgressMarker().getPosition());
        assertEquals(pointB,mockAdventureMap.getEndMarker().getPosition());

        try{
            AdventureDrawer.getBounds(null);
            assert(false);
        } catch(NullPointerException e){
            assert(true);
        }

        try{
            AdventureDrawer.getBounds(null,null);
            assert(false);
        } catch(NullPointerException e){
            assert(true);
        }
   }

    private void assertBoundsEqual(LatLngBounds expected, LatLngBounds actual){
        assertEquals(expected.northeast, actual.northeast);
        assertEquals(expected.southwest,actual.southwest);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

//        solo.finishOpenedActivities();
    }


}
