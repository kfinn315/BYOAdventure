package com.bingcrowsby.byoadventure.test;

import android.test.InstrumentationTestCase;

import com.bingcrowsby.byoadventure.LocationFactory;
import com.bingcrowsby.byoadventure.Model.AdventureDatasource;
import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.Model.DirectionsObject;
import com.bingcrowsby.byoadventure.Model.PointList;
import com.bingcrowsby.byoadventure.Statics;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


/**
* Created by kevinfinn on 1/30/15.
*/
    public class AdventureTestCase extends InstrumentationTestCase {

    AdventureDatasource datasource;

    long id1, id2;
    DirectionsObject directionsObject;
    AdventureObject originalAdv;
  //  RenamingDelegatingContext context;

    @Override
    public void setUp() throws Exception {
        super.setUp();
//        context = new RenamingDelegatingContext(, "test_");

            System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }
//        datasource = AdventureDatasource.getInstance(context);
//        datasource.open();
//        directionsObject = new DirectionsObject();
//        directionsObject.startPosition = new LatLng(123.4567,891.23456);
//        directionsObject.endPosition = new LatLng(0.1234,12.3456);
//        directionsObject.bounds = new LatLngBounds.Builder().include(directionsObject.startPosition).include(directionsObject.endPosition).build();
//        originalAdv = new AdventureObjectBuilder().name("TestAdventure").directions(directionsObject).build();
//        originalAdv.mdateCreated = new Date(Date.UTC(2014,3,2,10,0,0));
//        Log.i("test", "created "+originalAdv.mdateCreated.toString());
//        originalAdv.mdateUpdated = new Date();
//        Log.i("test", "updated "+originalAdv.mdateUpdated.toString());
//
//        id1 = datasource.saveAdventure(originalAdv);
//
//        id2 = datasource.insertAdventureObject(originalAdv);
//
//    }
//        public void testDatasource() throws Exception {
//            assertNotNull(datasource);
//
//            AdventureObject retrievedAdv = datasource.getAdventure(id1);
//
//            //compare
////            assertEquals(retrievedAdv.getDaysElapsed(),originalAdv.getDaysElapsed());
//            assertEquals((long) retrievedAdv.id, id1);
//            assertEquals(retrievedAdv.madventureTitle, originalAdv.madventureTitle);
//            assertEquals(retrievedAdv.mcurrentDistance, originalAdv.mcurrentDistance);
//            assertEquals(retrievedAdv.mdateCreated.getDate(), new Date().getDate());
//            assertEquals(retrievedAdv.mdistanceScale, originalAdv.mdistanceScale);
//            assertEquals(retrievedAdv.mtotalDistance, originalAdv.mtotalDistance);
//            assertEquals(retrievedAdv.mdateUpdated.getDate(), new Date().getDate());
//        }
//
//    public void testDates(){
//        assertNotNull(datasource);
//
//        AdventureObject retrievedAdv = datasource.getAdventure(id2);
//        Log.i("test", "retrieved created "+retrievedAdv.mdateCreated.toString());
//
//        Log.i("test", "retrieved updated "+retrievedAdv.mdateUpdated.toString());
//
//        assertEquals((long) retrievedAdv.id, id2);
//        assertEquals(retrievedAdv.madventureTitle, originalAdv.madventureTitle);
//        assertEquals(retrievedAdv.mcurrentDistance, originalAdv.mcurrentDistance);
//        assertEquals(retrievedAdv.mdateCreated.getDate(), originalAdv.mdateCreated.getDate());
//        assertEquals(retrievedAdv.mdistanceScale, originalAdv.mdistanceScale);
//        assertEquals(retrievedAdv.mtotalDistance, originalAdv.mtotalDistance);
//        assertEquals(retrievedAdv.mdateUpdated.getDate(), originalAdv.mdateUpdated.getDate());
//        assertEquals(retrievedAdv.getTimeElapsed(), System.currentTimeMillis() - originalAdv.mdateCreated.getTime());
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//
//        datasource.close();
//    }


//    public void testAdventureMap(){
//        AdventureMap adventureMap = new AdventureMap();
//
//        Marker marker = mock(Marker.class);
//
//        GoogleMap googleMap = mock(GoogleMap.class);
//        when(googleMap.addMarker((MarkerOptions)anyObject())).thenReturn(marker);
//        adventureMap.drawAdventure(context, googleMap);
//
//    }



    private void assertBoundsEqual(LatLngBounds expected, LatLngBounds actual){
        assertEquals(expected.northeast,actual.northeast);
        assertEquals(expected.southwest,actual.southwest);
    }
    public void testAdventureObject(){
        AdventureObject testObject = new AdventureObject();
        try {
            testObject.buildAdventureMap();
            assert(false);
        }
        catch(NullPointerException e){
            assert(true);
        }

        String snippet = testObject.getSnippet();
        assert(snippet == null);

        String days = testObject.getDaysElapsed();
        assertEquals("No start date", days);

        testObject.setCurrentDistance(0f);
        testObject.setTotalDistance(0f);
        assertEquals("0.0/0.0 meters", testObject.getDistanceString());

//        testObject.setCurrentDistance(0);
//        testObject.addProgress(-1);
//        assertEquals(testObject.getCurrentDistance(), 0);
    }

    public void testLocationFactory(){
        PointList pointList = new PointList();
        LatLng point1 = new LatLng(0.0,0.0);
        LatLng point2 = new LatLng(0.0,-.9);
        pointList.add(point1);
        pointList.add(point2);
        double distance = pointList.getDistanceToNextPoint();
        LocationFactory testfactory = new LocationFactory(null);
        assertFalse(testfactory.hasPointsList());

        testfactory.setPointsList(pointList);
        assertTrue(testfactory.hasPointsList());
        assertEquals(Statics.computeLineLength(point1,point2),testfactory.getCurrentLineLength());
        double progress = distance/2;

        assertEquals(distance,testfactory.getRemainingDistanceOnLine());
        testfactory.addProgress(progress);
        assertEquals(progress,testfactory.getDistanceProgressedOnLine());

        double progress2 = testfactory.getRemainingDistanceOnLine()/2;
        testfactory.addProgress(progress2);
        assertEquals(progress2,testfactory.getRemainingDistanceOnLine(),0.0001d);

        double progress3 = testfactory.getRemainingDistanceOnLine();
        testfactory.addProgress(progress3);
        assertEquals(0.0, testfactory.getRemainingDistanceOnLine());

        testfactory.addProgress(1);
        assertEquals(1, testfactory.getDirectionPoints().getCurrentIndex());
        assertEquals(0.0, testfactory.getRemainingDistanceOnLine());
    }

    public void testPointList(){
        PointList pointList = new PointList();
        LatLng pointA = new LatLng(45, -100);
        LatLng pointB = new LatLng(45, -099.8);
        LatLng pointC = new LatLng(35, -099.8);

        //test when the list is empty
        try {
            pointList.getStartPoint();
            assert(false);
        }
        catch(IndexOutOfBoundsException e){
            assert(true);
        }
        try {
            pointList.getEndPoint();
            assert(false);
        } catch(IndexOutOfBoundsException e){
            assert(true);
        }
        assertEquals(pointList.getCurrentIndex(), 0);
        assertFalse(pointList.hasNextPoint());
        assertFalse(pointList.moveToNextPoint());
        assert(pointList.getNextPoint()==null);
        pointList.setCurrentIndex(5);
        assert(pointList.getCurrentIndex()==0);
        pointList.setCurrentIndex(-1);
        assert(pointList.getCurrentIndex()==0);

        pointList= new PointList();
        //one point
        pointList.add(pointA);
        assertEquals(pointA, pointList.getStartPoint());
        assertEquals(pointA, pointList.getEndPoint());
        assert(pointList.size()==1);
        assertFalse(pointList.hasNextPoint());
        try {
            pointList.getNextPoint();
            assert(false);
        } catch(IndexOutOfBoundsException e){
            assert(true);
        }
        assertEquals(0, pointList.getCurrentIndex());
        assertEquals(pointA, pointList.getCurrentPoint());

        pointList.remove(0);
        assert(pointList.getCurrentIndex()==0);

        pointList = new PointList();
        //two points
        pointList.add(pointA);
        pointList.add(pointB);
        assertEquals(pointA, pointList.getStartPoint());
        assertEquals(pointB, pointList.getEndPoint());
        assertEquals(pointA, pointList.getCurrentPoint());
        assertEquals(pointB, pointList.getNextPoint());
        pointList.setCurrentIndex(0);
        assertEquals(Statics.computeLineLength(pointA,pointB),pointList.getDistanceToNextPoint());
        assert(pointList.hasNextPoint());
        assertEquals(0,pointList.getCurrentIndex());
        assert(pointList.moveToNextPoint());
        pointList.moveToNextPoint();
        assertEquals(1, pointList.getCurrentIndex());
        assertEquals(pointB, pointList.getCurrentPoint());
        assertEquals(0.0,pointList.getDistanceToNextPoint());

        assertEquals(1,pointList.getCurrentIndex());
        pointList.remove(0);
        assertEquals(0,pointList.getCurrentIndex());
        assertFalse(pointList.hasNextPoint());
        assertEquals(pointB,pointList.getCurrentPoint());

        pointList = new PointList();
        //add and remove
        pointList.add(pointA);
        pointList.add(pointC);
        pointList.setCurrentIndex(1);
        pointList.add(1,pointB);
        assertEquals(2,pointList.getCurrentIndex());
        assertEquals(pointC,pointList.getCurrentPoint());
        pointList.add(1,pointA);
        assertEquals(3,pointList.getCurrentIndex());
        assertEquals(pointC,pointList.getCurrentPoint());
        pointList.remove(2);
        assertEquals(2,pointList.getCurrentIndex());
        pointList.remove(0);
        assertEquals(1,pointList.getCurrentIndex());
        pointList.remove(0);
        assertEquals(0,pointList.getCurrentIndex());

        //invariant
        assert(pointList.getCurrentIndex()>=0 && pointList.getCurrentIndex()<pointList.size());
    }
}
