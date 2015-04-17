package com.bingcrowsby.byoadventure.Activity;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;

import com.bingcrowsby.byoadventure.GMapV2Direction;
import com.bingcrowsby.byoadventure.GetDirectionsAsyncTask;
import com.bingcrowsby.byoadventure.Model.AdventureDrawer;
import com.bingcrowsby.byoadventure.Model.AdventureMap;
import com.bingcrowsby.byoadventure.Model.DirectionsObject;
import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;

/**
 * Created by kevinfinn on 1/11/15.
 */
public class MapActivity extends ActionBarActivity {
    public GoogleMap mMap;
    AdventureMap mAdvMap;

    public void handleGetDirectionsResult(DirectionsObject result)
    {
        mAdvMap = new AdventureMap.Builder().linePoints(DirectionsObject.getPointListFromPolyline(result.polyline)).bounds(result.bounds).build();
        try {
            AdventureDrawer.drawAdventure(this, mMap, mAdvMap);
        } catch (Exception e){
            new AlertDialog.Builder(this).setTitle("Error").setMessage("Unable to draw adventure: "+e.getMessage()).show();
        }
    }

    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {

        HashMap<String,String> params = new HashMap<>();
        params.put(GetDirectionsAsyncTask.START_LAT,String.valueOf(fromPositionDoubleLat));
        params.put(GetDirectionsAsyncTask.START_LONG,String.valueOf(fromPositionDoubleLong));
        params.put(GetDirectionsAsyncTask.END_LAT,String.valueOf(toPositionDoubleLat));
        params.put(GetDirectionsAsyncTask.END_LONG,String.valueOf(toPositionDoubleLong));
        params.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, GMapV2Direction.MODE_WALKING);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(params);
    }
}
