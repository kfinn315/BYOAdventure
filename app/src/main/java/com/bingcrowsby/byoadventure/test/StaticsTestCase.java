package com.bingcrowsby.byoadventure.test;

import android.test.AndroidTestCase;

import com.bingcrowsby.byoadventure.Statics;

/**
* Created by kevinfinn on 2/17/15.
*/
public class StaticsTestCase extends AndroidTestCase {

    public StaticsTestCase() {
        super();
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

//        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());


    }

    public void testFormatLocation(){
        String result = Statics.formatLocation(null);
        assertEquals(null,result);


    }

    public void testListAdapter(){

    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

//        solo.finishOpenedActivities();


    }


}
