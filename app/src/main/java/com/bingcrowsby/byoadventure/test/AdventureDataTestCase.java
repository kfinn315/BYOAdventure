package com.bingcrowsby.byoadventure.test;

import android.test.AndroidTestCase;

import com.bingcrowsby.byoadventure.AdventureData;
import com.bingcrowsby.byoadventure.Model.AdventureDatasource;
import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.Model.PointList;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
* Created by kevinfinn on 2/17/15.
*/
public class AdventureDataTestCase extends AndroidTestCase {

    AdventureData adventureData;
    AdventureDatasource adventureDatasource;
    PointList pointList = null;

    public AdventureDataTestCase() {
        super();
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
        adventureData = AdventureData.getInstance(getContext());
        adventureDatasource = new AdventureDatasource(getContext());
        adventureDatasource.open();


        pointList = new PointList();
        LatLng point1 = new LatLng(0,0);
        LatLng point2 = new LatLng(1,0);
        pointList.add(point1);
        pointList.add(point2);

    }

    public void testClearInventory(){
        int size = adventureDatasource.getCount();
        int cleared = adventureData.clearAdventureInventory();
        assertEquals(size,cleared);
        assertEquals(0, adventureData.getAdventureCount());
        assertEquals(0, adventureDatasource.getCount());
    }

    public void testGetAdventureList(){
        List<AdventureObject> listActual = adventureData.getAdventureList();
        List<AdventureObject> listExpected = adventureDatasource.getAllAdventures();
        assert(listActual.equals(listExpected));
    }

    public void testGetAdventureFromId(){
        assertEquals(null,adventureData.getAdventureFromId(-1));
            AdventureObject object2 = new AdventureObject.Builder(pointList).name("test2").build();
        if(object2 == null)
            assert(false);
        long id = adventureDatasource.saveAdventure(object2);
        assert(object2.equals(adventureData.getAdventureFromId(id)));
    }

    public void testSaveAdventure(){
        AdventureObject object1 = new AdventureObject.Builder(pointList).name("test1").build();
        if(object1 == null)
            assert(false);
        long id = adventureData.saveAdventure(object1);
        AdventureObject retrievedObj1 = adventureDatasource.getAdventure(id);
        assert(retrievedObj1.equals(object1));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
//        adventureDatasource.close();
//        adventureData.close();

//        solo.finishOpenedActivities();


    }


}
