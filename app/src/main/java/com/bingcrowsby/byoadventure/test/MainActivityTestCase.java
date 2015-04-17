package com.bingcrowsby.byoadventure.test;

import android.app.Instrumentation;
import android.database.Cursor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bingcrowsby.byoadventure.Activity.MainActivity;
import com.bingcrowsby.byoadventure.Controller.AdventureAdapter;
import com.bingcrowsby.byoadventure.Model.AdventureDatasource;
import com.bingcrowsby.byoadventure.Model.AdventureDbOpenHelper;
import com.bingcrowsby.byoadventure.Model.AdventureObject;
import com.bingcrowsby.byoadventure.Model.PointList;
import com.bingcrowsby.byoadventure.R;
import com.google.android.gms.maps.model.LatLng;
import com.robotium.solo.Solo;

/**
* Created by kevinfinn on 2/17/15.
*/
public class MainActivityTestCase extends ActivityInstrumentationTestCase2 {
    Instrumentation instrumentation;
    MainActivity mainActivity;
    Solo solo;
    AdventureDatasource datasource;
    ListView listView;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.bingcrowsby.byoadventure.Activity.MainActivity";

    private static Class<?> launcherActivityClass;
    static{
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public MainActivityTestCase() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        solo = new Solo(getInstrumentation(),getActivity());
        mainActivity = (MainActivity)getActivity();

        listView = (ListView)mainActivity.findViewById(R.id.listview);

        datasource = AdventureDatasource.getInstance(getActivity());
        datasource.open();
    }

    public void testMainActivity(){
        Cursor cursor = datasource.getAdventureCursor();
        cursor.moveToFirst();
        for(int i = 0; i < datasource.getCount(); i++) {
            AdventureObject adventureObject = mainActivity.loadAdventure(i);
            assertEquals(adventureObject.id.intValue(), cursor.getInt(cursor.getColumnIndex(AdventureDbOpenHelper.ROW_ID)));
            assertEquals(mainActivity.getAdventureObject().id.intValue(),adventureObject.id.intValue());
            cursor.moveToNext();
        }


    }
    public void testListView(){

        for(int i=0; i < listView.getAdapter().getCount(); i++) {
            View view = getViewAtIndex(listView, i, getInstrumentation());
            TextView textView = (TextView) view.findViewById(R.id.text1);
            solo.clickOnView(textView, true);

            assertTrue("item position",listView.getSelectedItemPosition()==i);
            solo.waitForLogMessage("load adventure w/ index " + i, 2000);
            AdventureObject adventureObject = mainActivity.getAdventureObject();
            if(adventureObject != null)
                assertEquals("adventureObject",textView.getText(), adventureObject.madventureTitle);
            else
                assert(false);
//            solo.goBack();
        }
    }
    public void testAdventureAdapter(){
        final AdventureAdapter adventureAdapter = (AdventureAdapter) (((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter());
        assertEquals(datasource.getCount(), adventureAdapter.getCount());
        Cursor cursor = datasource.getAdventureCursor();
        int i = 0;
        cursor.moveToFirst();
        do {
            assertEquals(cursor.getInt(cursor.getColumnIndex(AdventureDbOpenHelper.ROW_ID)),adventureAdapter.getItem(i).id.intValue());
            assertEquals(cursor.getString(cursor.getColumnIndex(AdventureDbOpenHelper.ROW_TITLE)),adventureAdapter.getItem(i).madventureTitle);
            assertEquals(cursor.getPosition(),i);
            i++;
        } while(cursor.moveToNext());

        final int previousSize = adventureAdapter.getCount();
        PointList plist = new PointList();
        plist.add(new LatLng(0,0));
        plist.add(new LatLng(1,0));
        plist.add(new LatLng(2,0));
        AdventureObject object = new AdventureObject.Builder(plist).name("newobject").build();
        datasource.saveAdventure(object);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adventureAdapter.swapCursor(datasource.getAdventureCursor());
                int newSize = adventureAdapter.getCount();

                assertEquals(previousSize + 1, newSize);
            }
        });
    }

    public View getViewAtIndex(final ListView listElement, final int indexInList, Instrumentation instrumentation) {
        ListView parent = listElement;
        if (parent != null) {
            if (indexInList <= parent.getAdapter().getCount()) {
                scrollListTo(parent, indexInList, instrumentation);
                int indexToUse = indexInList - parent.getFirstVisiblePosition();
                return parent.getChildAt(indexToUse);
            }
        }
        return null;
    }

    public <T extends AbsListView> void scrollListTo(final T listView,
                                                     final int index, Instrumentation instrumentation) {
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(index);
            }
        });
        instrumentation.waitForIdleSync();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

//        solo.finishOpenedActivities();
    }


}
